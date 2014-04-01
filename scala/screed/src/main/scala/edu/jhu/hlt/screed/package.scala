package edu.jhu.hlt.screed

import edu.jhu.hlt.miser._

object `package` {
  implicit def ts2vts (ts: TextSpan) = new ValidatableTextSpan(ts)
  implicit def s2vs (s: Section) = new ValidatableSection(s)
}
