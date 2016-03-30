package edu.jhu.hlt.concrete.validation.ff.entity;

import java.util.List;

import edu.jhu.hlt.concrete.validation.ff.FlattenedMetadata;
import edu.jhu.hlt.concrete.validation.ff.UUIDable;

public interface ValidEntityMentionSet extends UUIDable, FlattenedMetadata {
  public List<ValidEntityMention> getEntityList();
}
