/**
 *
 */
package concrete.examples.scala

import scala.util.matching.Regex
import edu.jhu.hlt.concrete.AnnotationMetadata
import edu.jhu.hlt.concrete.Communication
import edu.jhu.hlt.concrete.SentenceSegmentationCollection
import scala.collection.JavaConversions._
import edu.jhu.hlt.concrete.SentenceSegmentation

import edu.jhu.hlt.concrete.TextSpan
import edu.jhu.hlt.concrete.Sentence

import edu.jhu.hlt.concrete.util.{ConcreteUtil, SuperTextSpan}
import scala.language.implicitConversions


/**
 * A tiny sentence splitting utility, mostly useful as an example as to how to create Concrete {@link Sentence}s from {@link Section}s.
 * 
 * @author max
 * 
 */
class SillySentenceSplitter (pattern: Regex) {
  implicit def superTextSpan(textSpan: TextSpan) = new SuperTextSpan(textSpan)

  def this() = this(SillySentenceSplitter.SillyPattern)
  
  def splitSentences(comm : Communication) : SentenceSegmentationCollection = {
    val newComm = new Communication(comm)
    val commText = newComm.getText()
    val sentSegmentations = newComm.getSectionSegmentation().getSectionList().map { section =>
      val ts = section.getTextSpan()
      val sentencesText = ts.getSpanText(commText)
      val sentenceList = this.createSentenceArrayFromRegex(sentencesText)
      
      new SentenceSegmentation()
        .setUuid(ConcreteUtil.generateUUID)
        .setSectionId(section.getUuid())
        .setSentenceList(sentenceList)
    }
    
    new SentenceSegmentationCollection()
      .setMetadata(SillySentenceSplitter.Metadata)
      .setSentSegList(sentSegmentations)
      
  }
  
  def createSentenceArrayFromRegex(text: String) : List[Sentence] = {
    (this.pattern findAllMatchIn text) map { sentence =>
      val ts = new TextSpan()
        .setStart(sentence.start)
        .setEnding(sentence.end)
      new Sentence().setUuid(ConcreteUtil.generateUUID).setTextSpan(ts)
    } toList
  }
}

object SillySentenceSplitter {
  val SillyPattern : Regex = "[a-zA-Z0-9 ']+[.?!]+".r
  
  val Metadata : AnnotationMetadata = new AnnotationMetadata()
                                            .setConfidence(1)
                                            .setTool("SillySentenceSplitter v1.0s")
                                            .setTimestamp(System.currentTimeMillis() / 1000)
}
