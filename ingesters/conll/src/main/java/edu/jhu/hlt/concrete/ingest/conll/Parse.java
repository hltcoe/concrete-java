package edu.jhu.hlt.concrete.ingest.conll;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Splits the parseBit pieces (also useful for PropBank and NER)
 *
 * NOTE: You cannot use this for coref because coref mentions may overlap.
 */
public class Parse {

  public static class Constituent {

    public int start, end;        // [inclusive, inclusive]
    public Constituent parent;
    private edu.jhu.hlt.concrete.Constituent cons;
    private Constituent leftChild, rightChild;

    public Constituent(int id, String tag, Constituent parent) {
      this(id, tag, parent, -1, -1);
    }

    public Constituent(int id, String tag, Constituent parent, int start, int end) {
      this.cons = new edu.jhu.hlt.concrete.Constituent();
      this.cons.setId(id);
      this.cons.setTag(tag);
      this.cons.setChildList(new ArrayList<>());
      this.parent = parent;
      this.start = start;
      this.end = end;
      if (parent != null)
        parent.addChild(this);
    }
  
    public void addChild(Constituent c) {
      if (leftChild == null)
        leftChild = c;
      rightChild = c;
      cons.addToChildList(c.cons.getId());
    }
  
    public int getStart() {
      if (start < 0)
        start = leftChild.getStart();
      return start;
    }
  
    public int getEnd() {
      if (end < 0)
        end = rightChild.getEnd();
      return end;
    }
  
    public String getTag() {
      return cons.getTag();
    }
  
    public edu.jhu.hlt.concrete.Constituent convertToConcrete() {
      cons.setStart(getStart());
      cons.setEnding(getEnd() + 1);
      return cons;
    }
  
    public Constituent getParent() {
      return parent;
    }
  }

  private List<String> tokens;
  private List<String[]> openTags;
  private List<Integer> numCloseTags;
  private Deque<Parse.Constituent> stack;
  private List<Parse.Constituent> nodes;
  private int cid;

  public Parse() {
    tokens = new ArrayList<>();
    openTags = new ArrayList<>();
    numCloseTags = new ArrayList<>();
    stack = new ArrayDeque<>();
    nodes = new ArrayList<>();
    cid = 0;
  }

  /**
   * @param parseTok is a piece of an S-expression, e.g. "(TOP(S(NP(" or
   *                 "(13|(12" or "*))))", specified in various CoNLL columns.
   * @param word may be null, but if not, a node will be added with this
   *             as the tag which is the child of the last node pushed
   *             onto the stack. If null, the resulting tree will only
   *             have nodes for the nodes specified by parens.
   */
  public void add(String parseTok, String word) {
    int i = tokens.size();
    tokens.add(parseTok);

    int last = parseTok.length() - 1;
    int star = parseTok.indexOf('*');
    if (star < 0) {
      assert parseTok.charAt(0) == '('
          && parseTok.charAt(last) == ')'
          && parseTok.length() >= 3 : "parseTok=" + parseTok + " word=" + word;
          star = last;
    }

    if (star > 0)
      openTags.add(parseTok.substring(1, star).split("\\("));
    else
      openTags.add(new String[] {});

    if (star < last)
      numCloseTags.add(last - star);
    else
      numCloseTags.add(0);

    for (String tag : getOpenTags()) {
      Parse.Constituent c = new Parse.Constituent(cid++, tag, stack.peek());
      c.start = i;
      nodes.add(c);
      stack.push(c);
    }

    if (word != null)
      nodes.add(new Parse.Constituent(cid++, word, stack.peek(), i, i));

    for (int j = 0; j < getNumCloseTags(); j++) {
      Parse.Constituent c = stack.pop();
      c.end = i;
    }
  }

  public void addAlt(String parseTok, String pos, String word) {

    int star = parseTok.indexOf('*');

    boolean debug = false;
    if (debug) System.out.println("before: parseTok=" + parseTok);
    parseTok = parseTok.substring(0, star) + "(" + pos + " " + word + ")" + parseTok.substring(star + 1);
    if (debug) System.out.println("after:  parseTok=" + parseTok);

    int i = tokens.size();
    tokens.add(parseTok);

    int space = parseTok.indexOf(' ');
    openTags.add(parseTok.substring(1, space).split("\\("));

    numCloseTags.add(Conll2011.count(')', parseTok));

    for (String tag : getOpenTags()) {
      if (debug) System.out.println("open tag: " + tag);
      Parse.Constituent c = new Parse.Constituent(cid++, tag, stack.peek());
      c.start = i;
      nodes.add(c);
      stack.push(c);
    }

    for (int j = 0; j < getNumCloseTags(); j++) {
      if (debug) System.out.println("close tag: " + stack.peek().getTag());
      Parse.Constituent c = stack.pop();
      c.end = i;
    }
  }

  public String[] getOpenTags() {
    return getOpenTags(openTags.size() - 1);
  }

  public String[] getOpenTags(int i) {
    return openTags.get(i);
  }

  public int getNumCloseTags() {
    return getNumCloseTags(numCloseTags.size() - 1);
  }

  public int getNumCloseTags(int i) {
    return numCloseTags.get(i);
  }

  public List<Parse.Constituent> getConstituents() {
    return nodes;
  }

}