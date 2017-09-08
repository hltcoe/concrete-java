package edu.jhu.hlt.concrete.ingesters.kbp2017;

public enum MentionType {

  NORMAL("mention"),
  CANONICAL_MENTION("canonical_mention"),
  NOMINAL_MENTION("nominal_mention"),
  PRONOMINAL_MENTION("pronominal_mention"),
  NORMALIZED_MENTION("normalized_mention"),
  ;

  private MentionType(String kbForm) {
    this.kbForm = kbForm;
  }

  private final String kbForm;

  public final String getKBForm() {
    return this.kbForm;
  }

  public static MentionType create(String mentionTypeString) {
    for (MentionType mt : MentionType.values()) {
      if (mt.kbForm.equals(mentionTypeString))
        return mt;
    }
    throw new IllegalArgumentException("mention type is not enumerated: " + mentionTypeString);
  }
}
