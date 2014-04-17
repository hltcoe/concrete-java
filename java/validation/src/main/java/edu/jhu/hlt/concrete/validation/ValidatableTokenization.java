/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.util.SuperCommunication;

/**
 * @author max
 *
 */
public class ValidatableTokenization extends AbstractAnnotation<Tokenization> {

  /**
   * @param annotation
   */
  public ValidatableTokenization(Tokenization annotation) {
    super(annotation);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    return new SuperCommunication(c)
      .sentIdToSentenceMap()
      .keySet()
      .contains(this.annotation.getSentenceId());
  }

  /**
   * Check:
   * <ol>
   * <li>UUID is valid</li>
   * <li>Metadata is set</li>
   * <li>Metadata is valid</li>
   * <li>TokenizationKind is set</li>
   * </ol>
   * 
   * <ul>
   * <li>If TokenizationKind == Lattice, check Lattice exists and List does not</li>
   * <li>If TokenizationKind == List, check List exists and Lattice does not; validate List[Token]</li>
   * </ul>
   */
  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    boolean basics = this.validateUUID(this.annotation.getUuid())
        && this.printStatus("Metadata must be set", this.annotation.isSetMetadata())
        && this.printStatus("Metadata must be valid", new ValidatableMetadata(this.annotation.getMetadata()).isValid())
        && this.printStatus("TokenizationKind must be set.", this.annotation.isSetKind());
    if (!basics) 
      return false;
    else {
      boolean validByType = true;
      if (this.annotation.getKind() == TokenizationKind.TOKEN_LATTICE)
        validByType = this.printStatus("Kind == LATTICE, so lattice must be set, AND list must NOT be set.", this.annotation.isSetLattice() && !this.annotation.isSetTokenList());
      
      else {
        validByType = this.printStatus("Kind == LIST, so list must be set, AND list must NOT be set.", this.annotation.isSetTokenList() && !this.annotation.isSetLattice())
            && this.printStatus("TokenList must not be empty.", this.annotation.getTokenListSize() > 0);
        
        if (!validByType)
          return false;
        else {
          NavigableSet<Integer> tokenIdSet = new TreeSet<Integer>();
          
          Iterator<Token> iter = this.annotation.getTokenListIterator();
          boolean validTokenIdx = true;
          while (validTokenIdx && iter.hasNext()) {
            Token current = iter.next();
            int idx = current.getTokenIndex();
          }
          
        }
      }
      
      if (!validByType)
        return false;
      else 
        return true;
      
    }
  }

}
