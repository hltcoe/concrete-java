/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package concrete.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * @author max
 *
 */
public interface ProxyCommunication {
  public String getText();
  public String getId();
  public List<ConcreteTextSpan> getTextSpans();
  public Optional<String> getHeadline();
  public Optional<String> getDateline();
  public long getMillis();
  public String getType();
}
