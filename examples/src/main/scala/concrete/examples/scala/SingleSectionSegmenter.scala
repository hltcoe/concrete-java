/**
 *
 */
package concrete.examples.scala

import edu.jhu.hlt.concrete.AnnotationMetadata
import edu.jhu.hlt.concrete.SectionSegmentation
import edu.jhu.hlt.concrete.Communication
import edu.jhu.hlt.concrete.UUID

import edu.jhu.hlt.concrete.TextSpan
import edu.jhu.hlt.concrete.Section
import edu.jhu.hlt.concrete.SectionKind

import edu.jhu.hlt.concrete.util.ConcreteUtil

/**
  * This object represents a way to apply a single `SectionSegmentation` with a single `Section` to a `Communication` object.
  * 
  * @author max
  *
  */
object SingleSectionSegmenter {
  /**
    * Return the `AnnotationMetadata` for this `SingleSectionSegmenter` (a constant value).
    */
  val Metadata: AnnotationMetadata = new AnnotationMetadata()
                                            .setConfidence(1)
                                            .setTool("SingleSectionSegmenter v2.0.0-scala")
                                            .setTimestamp(System.currentTimeMillis() / 1000)
  /**
    * Return a `SectionSegmentation` object with a single `Section`. The `Section`
    * contains a single `TextSpan` with a start at character 0 and an end at the 
    * length of the Communication's text value.
    * 
    * @param comm A `Communication` that will be used to generate the `SectionSegmentation`.
    * @return A `SectionSegmentation` as described above.
    * @since 2.0.0-SNAPSHOT
    * 
    * {{{
    * scala> val comm = new Communication().setText("hello world!")...
    * comm: edu.jhu.hlt.concrete.Communication = Communication(...)
    * 
    * scala> import concrete.examples.scala.SingleSectionSegmenter
    * import concrete.examples.scala.SingleSectionSegmenter
    * 
    * scala> val singleSectionSeg = SingleSectionSegmenter.sectionCommunication(comm)
    * singleSectionSeg: edu.jhu.hlt.concrete.SectionSegmentation = SectionSegmentation(...)
    * 
    * scala> singleSectionSeg.sectionList.get(0).textSpan
    * res9: edu.jhu.hlt.concrete.TextSpan = TextSpan(start:0, ending:12)
    * }}}
    */
  def sectionCommunication(comm : Communication) : SectionSegmentation = {
    val textSpan = new TextSpan().setStart(0).setEnding(comm.getText().length())
    val section = new Section()
      .setUuid(ConcreteUtil.generateUUID)
      .setTextSpan(textSpan)
      .setKind(SectionKind.OTHER)
      
    val ss = new SectionSegmentation().setMetadata(this.Metadata).setUuid(ConcreteUtil.generateUUID)
    ss.addToSectionList(section)
    ss
  }
}
