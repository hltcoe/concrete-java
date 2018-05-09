package edu.jhu.hlt.concrete.miscommunication.tokenized;

import java.util.Comparator;

public class TokenIndexComparator implements Comparator<JavaTaggedToken> {

  @Override
  public int compare(JavaTaggedToken o1, JavaTaggedToken o2) {
    int id1 = o1.getTokenIndex().orElseThrow(() -> new IllegalArgumentException("can't use this comparator with unset token indices: " + o1.toString()));
    int id2 = o2.getTokenIndex().orElseThrow(() -> new IllegalArgumentException("can't use this comparator with unset token indices: " + o2.toString()));
    return Integer.compare(id1, id2);
  }
}
