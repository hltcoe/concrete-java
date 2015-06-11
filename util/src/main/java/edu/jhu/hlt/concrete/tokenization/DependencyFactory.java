/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.Dependency;

/**
 *
 */
public class DependencyFactory {

  /**
   *
   */
  private DependencyFactory() {
    // TODO Auto-generated constructor stub
  }

  /**
   *
   * @param dependentTokenIdx the dependent token index
   * @return a {@link Dependency} with the dep field set
   */
  public static final Dependency create(final int dependentTokenIdx) {
    return new Dependency().setDep(dependentTokenIdx);
  }

  /**
   *
   * @param dependentTokenIdx the dependent token index
   * @param govDepRelation the relation between the governor token and the dependent token
   * @return a {@link Dependency} with the dep and edgeType fields set
   */
  public static final Dependency create(final int dependentTokenIdx, final String govDepRelation) {
    return create(dependentTokenIdx).setEdgeType(govDepRelation);
  }

  /**
   *
   * @param dependentTokenIdx the dependent token index
   * @param govDepRelation the relation between the governor token and the dependent token
   * @param govToken the index of the governor token
   * @return a {@link Dependency} with the dep, edgeType, and gov fields set
   */
  public static final Dependency create(final int dependentTokenIdx, final String govDepRelation, final int govToken) {
    return create(dependentTokenIdx, govDepRelation).setGov(govToken);
  }
}
