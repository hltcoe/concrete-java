package edu.jhu.hlt.concrete.validation.ff.entity;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.FlattenedMetadata;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;
import edu.jhu.hlt.concrete.validation.ff.Metadata;
import edu.jhu.hlt.concrete.validation.ff.UUIDs;
import edu.jhu.hlt.concrete.validation.ff.ValidUUID;

public class NecessarilyUniqueUUIDEntityMentionSet extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<EntityMentionSet>
    implements ValidEntityMentionSet {

  private final ValidUUID uuid;
  private final String tool;
  private final int kb;
  private final long ts;

  private final List<ValidEntityMention> ml;

  NecessarilyUniqueUUIDEntityMentionSet(EntityMentionSet ems) throws InvalidConcreteStructException {
    super(ems);
    this.uuid = UUIDs.validate(ems.getUuid());
    this.addNecessarilyUniqueUUID(this.uuid);

    FlattenedMetadata fmd = Metadata.validate(ems.getMetadata());
    this.tool = fmd.getTool();
    this.kb = fmd.getKBest();
    this.ts = fmd.getTimestamp();

    final int mls = ems.getMentionListSize();
    this.ml = new ArrayList<>(mls);
    if (mls > 0)
      for (EntityMention em : ems.getMentionList())
        this.ml.add(EntityMentions.validate(em));
  }

  @Override
  public ValidUUID getUUID() {
    return this.uuid;
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

  @Override
  public List<ValidEntityMention> getEntityList() {
    return new ArrayList<>(this.ml);
  }
}
