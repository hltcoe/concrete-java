package edu.jhu.hlt.concrete.validation.ff;

import edu.jhu.hlt.concrete.AnnotationMetadata;

public class FlatMetadataImpl implements FlattenedMetadata {

  private final String tool;
  private final int kb;
  private final long ts;

  FlatMetadataImpl(final AnnotationMetadata md) throws InvalidConcreteStructException {
    this.tool = md.getTool();
    this.kb = md.getKBest();
    if (this.kb <= 0)
      throw new InvalidConcreteStructException("KBest must be >0.");
    this.ts = md.getTimestamp();
  }

  @Override
  public String getTool() {
    return this.tool;
  }

  @Override
  public int getKBest() {
    return this.kb;
  }

  @Override
  public long getTimestamp() {
    return this.ts;
  }
}
