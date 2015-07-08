package edu.jhu.hlt.concrete.ingest.conll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A row of CoNLL input (corresponds to one word) */
public class Conll2011Row {

  public final String docId;                 // This is a variation on the document filename
  public final String part;                  // Some files are divided into multiple parts numbered as 000, 001, 002, ... etc.
  public final int wordNumber;
  private String word;          // Not public because you need a setter to work with *.skel files
  public final String pos;
  public final String parseBit;              // This is the bracketed structure broken before the first open parenthesis in the parse, and the word/part-of-speech leaf replaced with a *. The full parse can be created by substituting the asterix with the "([pos] [word])" string (or leaf) and concatenating the items in the rows of that column.
  public final String predicateLemma;        // The predicate lemma is mentioned for the rows for which we have semantic role information. All other rows are marked with a "-"
  public final String predicateFramesetId;   // This is the PropBank frameset ID of the predicate in Column 7.
  public final String wordSense;             // This is the word sense of the word in Column 3.
  public final String speakerOrAuthor;       // This is the speaker or author name where available. Mostly in Broadcast Conversation and Web Log data.
  public final String namedEntities;         // These columns identifies the spans representing various named entities.
  public final String[] predicateArguments;  // There is one column each of predicate argument structure information for the predicate mentioned in Column 7.
  private final String coref;                // Coreference chain information encoded in a parenthesis structure.

  public Conll2011Row(String line) {
    String[] toks = line.split("\\s+");
    docId = toks[0];
    part = toks[1];
    wordNumber = Integer.parseInt(toks[2]);
    word = toks[3];
    pos = toks[4];
    parseBit = toks[5];
    predicateLemma = toks[6];
    predicateFramesetId = toks[7];
    wordSense = toks[8];
    speakerOrAuthor = toks[9];
    namedEntities = toks[10];
    int n = toks.length;
    coref = toks[n - 1];
    predicateArguments = Arrays.copyOfRange(toks, 11, n - 1);
  }

  public String toString() {
    return word;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String w) {
    this.word = w;
  }

  public int getNumPredicates() {
    return predicateArguments.length;
  }

  public String getPredArg(int i) {
    return predicateArguments[i];
  }

  // coref field may look like one of the following:
  // (37
  // (34)|37)
  // (37
  // 37)
  // (34|(63
  // The reason the close tags have a cluster id is because these mentions
  // don't form a tree, they may cross.
  private List<String> corefClusterStarts, corefClusterEnds;

  public List<String> getCorefClusterStarts() {
    if (corefClusterStarts == null)
      populateCoref();
    return corefClusterStarts;
  }

  public List<String> getCorefClusterEnds() {
    if (corefClusterEnds == null)
      populateCoref();
    return corefClusterEnds;
  }

  private void populateCoref() {
    corefClusterStarts = new ArrayList<>();
    corefClusterEnds = new ArrayList<>();
    String[] pieces = coref.split("\\|");
    for (String p : pieces) {
      String id = p.replaceAll("\\(|\\)", "");

      // Cluster ids are not valid across parts!
      id = part + "-" + id;

      if (p.startsWith("("))
        corefClusterStarts.add(id);
      if (p.endsWith(")"))
        corefClusterEnds.add(id);
    }
  }

}
