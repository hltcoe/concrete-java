/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import java.util.Optional;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;

/**
 *
 */
public class ValidatableToken extends AbstractAnnotation<Token> {

  private final Optional<TextSpan> ots;
  private final Optional<TextSpan> rts;
  
  /**
   * @param annotation
   */
  public ValidatableToken(Token annotation) {
    super(annotation);
    
    this.ots = Optional.ofNullable(this.annotation.getTextSpan());
    this.rts = Optional.ofNullable(this.annotation.getRawTextSpan());
  }
  
  private static boolean validateTextSpan(Optional<TextSpan> ts) {
    boolean present = ts.isPresent();
    if (present)
      return new ValidatableTextSpan(ts.get()).isValid();
    else
      return true;
  }
  
  private static boolean validateTextSpan(Optional<TextSpan> ts, Communication c) {
    boolean present = ts.isPresent();
    if (present)
      return new ValidatableTextSpan(ts.get()).isValidWithComm(c);
    else
      return true;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    boolean wComm = validateTextSpan(ots, c)
        && validateTextSpan(rts, c);
    return wComm;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    boolean tkidx = this.printStatus("TokenIndex must be >= 0.", this.annotation.getTokenIndex() >= 0);
    return tkidx 
        && this.printStatus("TextSpan must be valid, if set.", validateTextSpan(ots))
        && this.printStatus("Original TextSpan must be valid, if set.", validateTextSpan(rts));
  }
}
