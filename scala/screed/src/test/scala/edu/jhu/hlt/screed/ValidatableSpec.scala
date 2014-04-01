package edu.jhu.hlt.screed

import org.specs2.mutable._
import org.specs2.specification.{AllExpectations, Scope}

import edu.jhu.hlt.miser._
import edu.jhu.hlt.screed._

class ValidatableSpec extends Specification {
  "Validatable.isValid method" should {
    "return true over multiple valid ValidatableTextSpans" in {
      val tsList = TextSpan(0, 2) :: TextSpan(2, 5) :: TextSpan(5, 100) :: List()
      val vtsL = tsList.map {ts => new ValidatableTextSpan(ts)}

      Validatable.allValid(vtsL) must beTrue
    }

    "return false for any bad TextSpans" in {
      val tsList = TextSpan(0, 2) :: TextSpan(-2, 5) :: TextSpan(5, 100) :: List()
      val vtsL = tsList.map {ts => new ValidatableTextSpan(ts)}

      Validatable.allValid(vtsL) must beFalse
    }
  }
}
