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
 * @author max
 *
 */
object SingleSectionSegmenter {
  val Metadata: AnnotationMetadata = new AnnotationMetadata()
                                            .setConfidence(1)
                                            .setTool("SingleSectionSegmenter v1.0s")
                                            .setTimestamp(System.currentTimeMillis() / 1000)
  
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
