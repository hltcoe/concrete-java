/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.annotatednyt;


/**
 *
 */
class StringStringTuple {

  final String s1;
  final String s2;
  
  StringStringTuple(String s1, String s2) {
    this.s1 = s1;
    this.s2 = s2;
  }
  
  public String getS1() {
    return this.s1;
  }
  
  public String getS2() {
    return this.s2;
  }
  
  static StringStringTuple create(String s1, String s2) {
    return new StringStringTuple(s1, s2);
  }
}
