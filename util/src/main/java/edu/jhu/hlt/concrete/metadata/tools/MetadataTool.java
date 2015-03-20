/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.metadata.tools;

import java.util.List;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 * Interface that allows standardized construction and parsing of
 * {@link AnnotationMetadata} tool names.
 */
public interface MetadataTool {

  /**
   * @return the name of the tool.
   */
  public String getToolName();

  /**
   * @return the version of the tool.
   */
  public String getToolVersion();

  /**
   * @return a {@link List} of {@link String}s that represent notes about the tool.
   * For example, "used for experiment X".
   */
  public List<String> getToolNotes();
}
