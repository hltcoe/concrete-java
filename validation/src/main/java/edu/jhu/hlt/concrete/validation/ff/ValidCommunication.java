package edu.jhu.hlt.concrete.validation.ff;

import java.util.List;

import edu.jhu.hlt.concrete.validation.ff.entity.ValidEntitySet;

public interface ValidCommunication extends UUIDable {
  public List<ValidEntitySet> getEntitySetList();
}
