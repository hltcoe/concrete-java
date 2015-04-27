/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.annotatednyt;


/**
 *
 */
class StringStringStringTuple {

  final String s1;
  final String s2;
  final String s3;
  
  StringStringStringTuple(String s1, String s2, String s3) {
    this.s1 = s1;
    this.s2 = s2;
    this.s3 = s3;
  }
  
  public String getS1() {
    return this.s1;
  }
  
  public String getS2() {
    return this.s2;
  }
  
  public String getS3() {
    return this.s3;
  }
  
  static StringStringStringTuple create(String s1, String s2, String s3) {
    return new StringStringStringTuple(s1, s2, s3);
  }
}
