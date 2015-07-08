/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
/**
 * Contains package utility classes and exceptions.
 * 
 * This package exists to avoid "write time" errors that can occur when {@link edu.jhu.hlt.concrete.AnnotationMetadata}
 * is present, but the required fields on the object are unset. Utilizing this interface allows
 * compile-time detection of these errors.
 */
package edu.jhu.hlt.concrete.safe;
