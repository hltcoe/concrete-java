/**
  *  Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
  *  This software is released under the 2-clause BSD license.
  *  See LICENSE in the project root directory.
  */
package edu.jhu.hlt.concrete


object `package` {
  implicit def c2sc (c: Communication) = new SuperCommunication(c)
}
