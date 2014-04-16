/*
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.UUID;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;

import edu.jhu.hlt.concrete.Communication;

/**
 * Abstract validation class.
 * 
 * @author max
 */
public abstract class AbstractAnnotation<T extends TBase<T, ? extends TFieldIdEnum>> {

  protected final T annotation;
  
  /**
   * 
   */
  public AbstractAnnotation(T annotation) {
    this.annotation = annotation;
  }

  /**
   * Implementors should implement this method, which will be called to 
   * attempt to validate the annotation object. Called by {@link #validate(Communication)}.
   * 
   * @see #validate(Communication)
   * @param c - A communication associated with the {@link AbstractAnnotation}.
   * @return true if valid.
   */
  protected abstract boolean isValid(Communication c);
  
  /**
   * Annotations also have a notion of 'validity' on their own. For example,
   * a {@link UUID} string may not be a correct UUID string, thus invalidating
   * the individual annotation.
   * 
   * @return <code>true</code> if the individual annotation is valid. 
   */
  public abstract boolean isValid();
  
  /**
   * Public validation interface. 
   * 
   * @param c a {@link Communication} to validate
   * @return <code>true</code> if both the individual annotation is valid
   * and it is valid in context of the passed in {@link Communication} object.
   */
  public boolean validate(Communication c) {
    return this.isValid() && this.isValid(c);
  }
  
  /**
   * Get the annotation. 
   */
  public T getAnnotation() {
    return this.annotation;
  }
}
