package edu.jhu.hlt.concrete.sql

import  org.eclipse.xtend.lib.annotations.Data

@Data class BasicPackedCommunication {
  String id
  String type
  byte[] commBytes
}
