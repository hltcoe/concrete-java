package edu.jhu.hlt.screed

import edu.jhu.hlt.concrete.util.{Util}
import edu.jhu.hlt.miser._
import org.apache.thrift._

import com.twitter.scrooge.ThriftStruct

object Validatable {
  def allValid [T <: ThriftStruct] (i: List[Validatable[T]], comm: Communication) : Boolean =
    i.par.forall(item => item.isValid(comm))

  def allValid [T <: ThriftStruct] (i: List[Validatable[T]]) : Boolean =
    i.par.forall(x => x.isValid)
}

sealed abstract class Validatable[T <: ThriftStruct] (annotation: T) {

  /**
    * Public "validation" method, given a `Communication` context.
    */
  def isValid (comm: Communication) : Boolean

  /**
    * Public "validation" method, with no `Communication` context.
    */
  def isValid : Boolean
}

case class ValidatableTextSpan (annotation: TextSpan)
    extends Validatable[TextSpan](annotation) {

  override def isValid = annotation.start >= 0 & annotation.ending > annotation.start

  override def isValid(comm: Communication) = {
    val ending = this.annotation.ending
    val commTextLength = comm.text.getOrElse("").length

    this.isValid & ending <= commTextLength
  }
}

case class ValidatableParse(a: Parse) extends Validatable[Parse](a) {
  override def isValid = {
    val validId = Util.isValidUUIDString(a.uuid)
    val consts = a.constituentList
    val intIds = consts.map { c => c.id }

    // if there are any duplicate constituents, this parse is invalid.
    lazy val diffLenOK = (intIds diff intIds.distinct).length == 0

    // if this constituent is a leaf,
    // OR it dominates constituents with IDs > its own,
    // it is a valid domination.
    lazy val validDomination = consts.forall { const =>
      const.childList.length == 0 | const.childList.forall { childIdInt => childIdInt > const.id }
    }

    validId & diffLenOK & validDomination
  }

  override def isValid(comm: Communication) = ???
}

case class ValidatableSectionSegmentation (a: SectionSegmentation)
    extends Validatable[SectionSegmentation](a) {

  override def isValid = {
    lazy val validId = Util.isValidUUIDString(a.uuid)
    lazy val validSections = a.sectionList.length == 0 | a.sectionList.forall { s => s.isValid }

    validId & validSections
  }

  override def isValid(comm: Communication) = {
    lazy val indivValid = this.isValid
    lazy val cValidSections = a.sectionList.length == 0 | a.sectionList.forall { s => s.isValid(comm) }

    indivValid & cValidSections
  }
}

case class ValidatableSection (a: Section)
    extends Validatable[Section](a) {

  override def isValid = {
    lazy val validId = Util.isValidUUIDString(a.uuid)
    lazy val emptyOrValidTS = a.textSpan.isEmpty | a.textSpan.get.isValid

    validId & emptyOrValidTS
  }

  override def isValid(comm: Communication) = this.isValid
}
