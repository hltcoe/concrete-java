package edu.jhu.hlt.concrete.analytics.base;

import java.util.List;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.miscommunication.comms.TextCommunication;

/**
 * A zoner takes in a {@link String} and produces a {@link List} of
 * {@link Section}s.
 */
public interface Zoner {
  public List<Section> zone(TextCommunication comm) throws AnalyticException;
}
