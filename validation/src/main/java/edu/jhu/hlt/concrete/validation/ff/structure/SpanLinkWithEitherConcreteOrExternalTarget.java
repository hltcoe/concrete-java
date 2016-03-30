package edu.jhu.hlt.concrete.validation.ff.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.jhu.hlt.concrete.SpanLink;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;
import edu.jhu.hlt.concrete.validation.ff.UUIDs;
import edu.jhu.hlt.concrete.validation.ff.ValidUUID;

public class SpanLinkWithEitherConcreteOrExternalTarget implements ValidSpanLink {

  private final Optional<String> extTarget;
  private final Optional<ValidUUID> concTarget;
  private final String lType;

  SpanLinkWithEitherConcreteOrExternalTarget(final SpanLink sl, final ValidTokenization ptr) throws InvalidConcreteStructException {
    TokenRefSequence trs = sl.getTokens();

    this.extTarget = Optional.ofNullable(sl.getExternalTarget());
    this.concTarget = Optional.ofNullable(UUIDs.validate(sl.getConcreteTarget()));
    this.lType = sl.getLinkType();
  }

  @Override
  public List<Token> getTokens() {
    // TODO Auto-generated method stub
    return new ArrayList<>();
  }

  @Override
  public Optional<Token> getAnchorToken() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public ValidUUID getTokenizationUUID() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<TextSpan> getTextSpan() {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public Optional<String> getExternalTarget() {
    return this.extTarget;
  }

  @Override
  public Optional<ValidUUID> getConcreteTarget() {
    return this.concTarget;
  }

  @Override
  public String getLinkType() {
    return this.lType;
  }

  @Override
  public SpanLink toConcrete() {
    // TODO Auto-generated method stub
    return null;
  }
}
