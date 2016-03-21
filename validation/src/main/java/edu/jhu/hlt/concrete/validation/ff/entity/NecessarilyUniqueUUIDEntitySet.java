package edu.jhu.hlt.concrete.validation.ff.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;
import edu.jhu.hlt.concrete.validation.ff.UUIDs;
import edu.jhu.hlt.concrete.validation.ff.ValidUUID;

public class NecessarilyUniqueUUIDEntitySet extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<EntitySet>
    implements ValidEntitySet {

  private final ValidUUID uuid;
  private final String tool;
  private final int kb;
  private final long ts;

  private final List<ValidEntity> el;
  private final Optional<ValidUUID> mentionSetUUID;

  NecessarilyUniqueUUIDEntitySet(EntitySet es) throws InvalidConcreteStructException {
    super(es);
    this.uuid = UUIDs.validate(es.getUuid());
    this.addNecessarilyUniqueUUID(this.uuid);
    AnnotationMetadata md = es.getMetadata();
    this.tool = md.getTool();
    this.kb = md.getKBest();
    this.ts = md.getTimestamp();

    final int els = es.getEntityListSize();
    this.el = new ArrayList<>(els);
    if (els > 0)
      for (Entity e : es.getEntityList())
        this.el.add(Entities.validate(e));

    if (es.isSetMentionSetId())
      this.mentionSetUUID = Optional.of(UUIDs.validate(es.getMentionSetId()));
    else
      this.mentionSetUUID = Optional.empty();
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
  public List<ValidEntity> getEntityList() {
    return new ArrayList<>(this.el);
  }

  @Override
  public Optional<ValidUUID> getMentionSetUUID() {
    return this.mentionSetUUID;
  }
}
