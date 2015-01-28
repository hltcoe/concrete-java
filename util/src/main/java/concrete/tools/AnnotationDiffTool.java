/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package concrete.tools;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;

import edu.jhu.hlt.concrete.Communication;

/**
 * @author max
 *
 */
public interface AnnotationDiffTool<T extends TBase<T, ? extends TFieldIdEnum>> extends AnnotationTool {
  /**
   * Generate an annotation 'diff' based on this tool's generic type (roughly: any Thrift object).
   * 
   * @param c - the {@link Communication} to annotate
   * @return the <b>T</b> this tool produced
   * @throws AnnotationException if there was an error during annotation.
   */
  public T annotateDiff(Communication c) throws AnnotationException;
}
