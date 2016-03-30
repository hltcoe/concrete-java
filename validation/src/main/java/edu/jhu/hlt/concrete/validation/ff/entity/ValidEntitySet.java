package edu.jhu.hlt.concrete.validation.ff.entity;

import java.util.List;
import java.util.Optional;

import edu.jhu.hlt.concrete.validation.ff.FlattenedMetadata;
import edu.jhu.hlt.concrete.validation.ff.UUIDable;
import edu.jhu.hlt.concrete.validation.ff.ValidUUID;

public interface ValidEntitySet extends UUIDable, FlattenedMetadata {
  public List<ValidEntity> getEntityList();

  public Optional<ValidUUID> getMentionSetUUID();
}
