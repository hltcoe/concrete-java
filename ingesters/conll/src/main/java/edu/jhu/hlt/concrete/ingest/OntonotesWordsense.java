package edu.jhu.hlt.concrete.ingest;

public class OntonotesWordsense {

  public final String file;
  public final int sentence;
  public final int word;
  public final String lemma;
  public final String sense;
  public final boolean hasTwoQuestionMark;  // manual doesn't explain what this means

  public OntonotesWordsense(String s) {
    String[] toks = s.split(" ");
    if (toks.length != 5 && toks.length != 6)
      throw new IllegalArgumentException("s=" + s);
    int i = 0;
    file = toks[i++];
    sentence = Integer.parseInt(toks[i++]);
    word = Integer.parseInt(toks[i++]);
    lemma = toks[i++];
    hasTwoQuestionMark = toks.length == 6;
    sense = toks[toks.length - 1];
  }

  /** Returns a string like "lemma-pos-sense", e.g. "throw-v-1" */
  public String getLemmaAndSense() {
    return lemma + "-" + sense;
  }

  @Override
  public String toString() {
    return "<WordSense lemma=" + lemma + " sense=" + sense
        + " file=" + file
        + " sentence=" + sentence + " word=" + word + ">";
  }
}
