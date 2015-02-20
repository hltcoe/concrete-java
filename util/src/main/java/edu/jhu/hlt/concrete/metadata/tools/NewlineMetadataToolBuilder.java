/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.metadata.tools;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 * Class that builds {@link MetadataTool} objects, split by newline.
 * Useful for creating parseable {@link AnnotationMetadata} tool names.
 */
public class NewlineMetadataToolBuilder {

  private static final String NIX_NEWLINE = "\n";

  /**
   * @param tool the {@link MetadataTool} used to build the String
   * @return a String suitable for use in {@link AnnotationMetadata#setTool(String)}.
   */
  public static final String generateToolString(MetadataTool tool) {
    final StringBuilder sb = new StringBuilder();
    sb.append(tool.getToolName());
    sb.append(NIX_NEWLINE);
    sb.append(tool.getToolVersion());
    sb.append(NIX_NEWLINE);
    tool.getToolNotes().forEach(s -> {
      sb.append(s);
      sb.append(NIX_NEWLINE);
    });

    return sb.toString();
  }
}
