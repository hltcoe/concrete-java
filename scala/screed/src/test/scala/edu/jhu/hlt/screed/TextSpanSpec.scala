package edu.jhu.hlt.screed

import org.specs2.mutable._
import org.specs2.specification.{AllExpectations, Scope}

import edu.jhu.hlt.miser._
import edu.jhu.hlt.screed._

object TestUtil {

}

class TextSpanSpec extends Specification {
  "ValidatableTextSpan's isValid method" should {
    "return false when the end < beginning" in {
      TextSpan(1000, 500).isValid must beFalse
    }

    "return false when the beginning is < 0" in {
      TextSpan(-1, 500).isValid must beFalse
    }

    "return false when the ending is < 0" in {
      TextSpan(0, -50).isValid must beFalse
    }

    "return true when the ending is > beginning and both are non-negative" in {
      TextSpan(1, 50).isValid must beTrue
    }
  }
}
