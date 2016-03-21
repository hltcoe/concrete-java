package edu.jhu.hlt.concrete.validation.ff;

import java.util.List;
import java.util.Optional;

import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;

public interface FlattenedTokenRefSequence {
  public List<Token> getTokens();

  public Optional<Token> getAnchorToken();

  public ValidUUID getTokenizationUUID();

  public Optional<TextSpan> getTextSpan();
}
