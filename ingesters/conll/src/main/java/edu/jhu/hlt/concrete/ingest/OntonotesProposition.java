package edu.jhu.hlt.concrete.ingest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.jhu.hlt.tutils.PennTreeReader;
import edu.jhu.prim.tuple.Pair;

/**
 * Represents a row in an OntoNotes *.prop file, or an SRL instance.
 *
 * @author travis
 */
public class OntonotesProposition {

  public static boolean DEBUG = false;

  public final String filename;
  public final int sentence;
  public final int terminal;
  public final String tagger;
  public final String predicateString;  // e.g. "look-v" (not actually in the manual, but appears in the data)
  public final String frameset;         // e.g. "look.02"
  public final Proplabel predicate;
  public final List<Proplabel> args;

  public OntonotesProposition(String line, boolean match_ON4_manual_exactly) {
    if (DEBUG)
      System.out.println("DEBUG: line=\"" + line + "\"");
    String[] toks = line.split(" ");
    int i = 0;
    filename = toks[i++];
    sentence = Integer.parseInt(toks[i++]);
    terminal = Integer.parseInt(toks[i++]);
    tagger = toks[i++];
    predicateString = match_ON4_manual_exactly ? null : toks[i++];
    frameset = toks[i++];
    String sep = toks[i++];
    assert "-----".equals(sep);
    predicate = new Proplabel(toks[i++]);
    args = new ArrayList<>();
    while (i < toks.length)
      args.add(new Proplabel(toks[i++]));
  }

  /**
   * @return a string like "look-v-2" which specifies the predicate lemma, POS,
   * and sense.
   */
  public String getPredicateString() {
    if (predicateString == null)
      throw new RuntimeException("no predicateString => no POS");
    int i = predicateString.lastIndexOf('-');
    String pos = predicateString.substring(i + 1);
    String[] lemma_sense = frameset.split("\\.");
    if (lemma_sense.length != 2)
      throw new RuntimeException("lemma_sense=" + Arrays.toString(lemma_sense));
    assert lemma_sense[0].equals(predicateString.substring(0, i));
    return lemma_sense[0] + "-" + pos + "-" + Integer.parseInt(lemma_sense[1]);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<Prop>\n");
    sb.append("  file=" + filename + "\n");
    sb.append("  sentence=" + sentence + "\n");
    sb.append("  terminal=" + terminal + "\n");
    sb.append("  tagger=" + tagger + "\n");
    sb.append("  frameset=" + frameset + "\n");
    sb.append("  predicate=" + predicate + "\n");
    sb.append("  arguments=" + args + "\n");
    sb.append("</Prop>\n");
    return sb.toString();
  }

  /**
   * Represents a single argument to a predicate (or the predicate itself).
   *
   * NOTE: This class does not store the trace information if there is any, it
   * just takes the final surface-form position of the moved constituent.
   */
  public static class Proplabel {

    public static final Pattern form1 = Pattern.compile("(\\d+):(\\d+)");
    // form2 = form1(\*form1)+    => trace path
    // form3 = form1(,form1)+     => gapped phrase
    // form4 = form3(\*form1)+    => both 2 and 3
    // form5 = form1;form1        => "indicates that either of the two nodes represent an ICH node."

    // terminal and height are arrays to handle split arguments.
    private int[] terminal;
    private int[] height;
    private String arg; // can be null for the predicate, otherwise e.g. "ARG2"
    private String argFeatures; // may be null, but is used e.g. for ARGM
    private boolean containedTrace; // traces aren't stored, but this will say if there was one originally

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("<Proplabel " + arg);
      if (argFeatures != null)
        sb.append("-" + argFeatures);
      for (int i = 0; i < terminal.length; i++)
        sb.append(" " + terminal[i] + ":" + height[i]);
      sb.append('>');
      return sb.toString();
    }

    /**
     * Traces aren't stored, but this will return true if there originally
     * was a trace (in any of the splits of this argument).
     */
    public boolean containedTrace() {
      return containedTrace;
    }

    /** Returns true if this is a split argument */
    public boolean isSplit() {
      return terminal.length > 1;
    }

    /** Returns the arg label (e.g. "ARG2") if an argument and null if a predicate */
    public String getLabel() {
      if (argFeatures != null)
        return arg + "-" + argFeatures;
      return arg;
    }

    /** Returns the arg features if they exist (e.g. "LOC" for arg="ARGM") or null otherwise. */
    public String getArgFeatures() {
      assert arg != null;
      return argFeatures;
    }

    public int getNumSplits() {
      assert terminal.length == height.length;
      return terminal.length;
    }

    /** You can only call this for non-split arguments */
    public int getTerminal() {
      if (terminal.length > 1)
        throw new RuntimeException("this arg is split");
      return terminal[0];
    }

    public int getTerminal(int i) {
      if (i < 0)
        i = terminal.length + i;
      return terminal[i];
    }

    /** You can only call this for non-split arguments */
    public int getHeight() {
      if (height.length > 1)
        throw new RuntimeException("this arg is split");
      return height[0];
    }

    public int getHeight(int i) {
      if (i < 0)
        i = terminal.length + i;
      return height[i];
    }

    public Proplabel(String s) {
      String[] pieces = s.split("-");
      if (pieces.length != 2 && pieces.length != 3)
        throw new IllegalArgumentException("s=\"" + s + "\"");;
      parsePosition(pieces[0]);
      arg = pieces[1];
      if (pieces.length == 3)
        argFeatures = pieces[2];
    }

    /**
     * If this is a split label and all of the splits form a contiguous span,
     * then return return a inclusive pair of [start,end] token indices (not
     * constituent indices). Returns null otherwise.
     */
    public Pair<Integer, Integer> getSplitsAsContiguousSpan(PennTreeReader.Indexer tree) {
      if (terminal.length == 1) {
        PennTreeReader.Node n = tree.get(getTerminal(), getHeight());
        int s = tree.getFirstToken(n);
        int e = tree.getLastToken(n);
        return new Pair<>(s, e);
      }

      // Get a list of ids for this split-arg
      List<Integer> all = new ArrayList<>();
      for (int i = 0; i < terminal.length; i++) {
        PennTreeReader.Node n = tree.get(terminal[i], height[i]);
        addAllTerms(n, all);
      }

      // Get a list of ids for all the leaf nodes
      List<Integer> leaves = new ArrayList<>();
      for (PennTreeReader.Node n : tree.getLeaves(true))
        leaves.add(n.id);

      // See if you can find all as a sublist of leaves
      int s = leaves.indexOf(all.get(0));
      for (int i = 1; i < terminal.length; i++) {
        int x = all.get(i);
        int y = leaves.get(s + i);
        if (x != y) {
          if (DEBUG) {
            System.out.println(tree.getRoot().getTreeString());
            for (int j = 0; j < terminal.length; j++) {
              PennTreeReader.Node n = tree.get(terminal[j], height[j]);
              System.out.print("piece[" + j + "] = " + terminal[j] + "-" + height[j]);
              System.out.print("\t first=" + tree.getFirstToken(n));
              System.out.println(" last=" + tree.getLastToken(n));
            }
          }
          return null;
        }
      }
      PennTreeReader.Node first = tree.getById(all.get(0));
      PennTreeReader.Node last = tree.getById(all.get(all.size() - 1));
      assert tree.getFirstToken(first) == tree.getLastToken(first);
      assert tree.getFirstToken(last) == tree.getLastToken(last);
      return new Pair<>(tree.getFirstToken(first), tree.getLastToken(last));
    }
    private void addAllTerms(PennTreeReader.Node node, List<Integer> addTo) {
      if (node.isLeaf()) {
        addTo.add(node.id);
      } else {
        for (PennTreeReader.Node c : node.getChildren())
          addAllTerms(c, addTo);
      }
    }

    private void parsePosition(String s) {
      Matcher m;
      int i;

      // Check if there is a trace at all
      containedTrace = (s.indexOf('*') >= 0);

      // Take the last element in the trace
      // check for form5
      i = s.indexOf(';');
      if (i >= 0) {
        s = s.substring(i + 1);
      } else {
        // check for form2/form4
        i = s.indexOf('*');
        if (i >= 0) {
          s = s.substring(i + 1);
        }
      }

      // check for form3
      String[] nodes = s.split(",");
      int n = nodes.length;
      terminal = new int[n];
      height = new int[n];
      for (i = 0; i < n; i++) {
        m = form1.matcher(nodes[i]);
        boolean b = m.find();
        assert b;
        terminal[i] = Integer.parseInt(m.group(1));
        height[i] = Integer.parseInt(m.group(2));
      }
    }
  }

  public static void main(String[] args) {
    String example = "wsj/00/wsj_0020.mrg@wsj@en@on 0 29 gold watch.01 ----- 24:1*25:0-LINK-SLC 26:1-ARG0 29:0-rel 30:0*25:0-ARG1 31:1-ARGM-CAU";
    System.out.println(new OntonotesProposition(example, true));

    int good = 0, bad = 0;
    File exampleFile = new File("/home/travis/code/fnparse/data/ontonotes-release-4.0/data/files/data/english/annotations/bc/cnn/00/cnn_0000.prop");
    try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(exampleFile)))) {
      while (r.ready()) {
        String line = r.readLine();
        try {
          System.out.println(new OntonotesProposition(line, false));
          good++;
        } catch (Exception e) {
          bad++;
          System.out.println("COULDN'T PARSE!!! " + e.getMessage());
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println("good=" + good + " bad=" + bad);
  }
}
