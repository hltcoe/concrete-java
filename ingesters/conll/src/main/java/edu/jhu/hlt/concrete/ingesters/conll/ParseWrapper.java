package edu.jhu.hlt.concrete.ingesters.conll;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import edu.jhu.hlt.concrete.Constituent;

/**
 * Splits the parseBit pieces (also useful for PropBank and NER)
 *
 * NOTE: You cannot use this for coref because coref mentions may overlap.
 */
public class ParseWrapper {

  public static class ConstituentWrapper {

    public int start, end;        // [inclusive, inclusive]
    public ConstituentWrapper parent;
    private Constituent cons;
    private ConstituentWrapper leftChild, rightChild;

    public ConstituentWrapper(int id, String tag, ConstituentWrapper parent) {
      this(id, tag, parent, -1, -1);
    }

    public ConstituentWrapper(int id, String tag, ConstituentWrapper parent, int start, int end) {
      this.cons = new Constituent();
      this.cons.setId(id);
      this.cons.setTag(tag);
      this.cons.setChildList(new ArrayList<>());
      this.parent = parent;
      this.start = start;
      this.end = end;
      if (parent != null)
        parent.addChild(this);
    }

    public void addChild(ConstituentWrapper c) {
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

    public Constituent convertToConcrete() {
      cons.setStart(getStart());
      cons.setEnding(getEnd() + 1);
      return cons;
    }

    public ConstituentWrapper getParent() {
      return parent;
    }
  }

  private List<String> tokens;
  private List<String[]> openTags;
  private List<Integer> numCloseTags;
  private Deque<ParseWrapper.ConstituentWrapper> stack;
  private List<ParseWrapper.ConstituentWrapper> nodes;
  private int cid;

  public ParseWrapper() {
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
      ParseWrapper.ConstituentWrapper c = new ParseWrapper.ConstituentWrapper(cid++, tag, stack.peek());
      c.start = i;
      nodes.add(c);
      stack.push(c);
    }

    if (word != null)
      nodes.add(new ParseWrapper.ConstituentWrapper(cid++, word, stack.peek(), i, i));

    for (int j = 0; j < getNumCloseTags(); j++) {
      ParseWrapper.ConstituentWrapper c = stack.pop();
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
      ParseWrapper.ConstituentWrapper c = new ParseWrapper.ConstituentWrapper(cid++, tag, stack.peek());
      c.start = i;
      nodes.add(c);
      stack.push(c);
    }

    for (int j = 0; j < getNumCloseTags(); j++) {
      if (debug) System.out.println("close tag: " + stack.peek().getTag());
      ParseWrapper.ConstituentWrapper c = stack.pop();
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

  public List<ParseWrapper.ConstituentWrapper> getConstituents() {
    return nodes;
  }
}