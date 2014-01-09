/**
 *
 */
package concrete.examples.scala

import scala.util.matching.Regex
import scala.collection.JavaConversions._
import edu.jhu.hlt.concrete._

import edu.jhu.hlt.concrete.util.{ConcreteUtil, SuperTextSpan}
import scala.language.implicitConversions


/**
  * A tiny sentence splitting class, mostly useful as an example as to how to create Concrete `Sentence` objects from `Section` objects.
  * 
  * @constructor pass in a `Regex` to use to split sentences (optional).
  * @author max
  * 
  */
class SillySentenceSplitter (pattern: Regex) {
  /**
    * An implicit converter for converting `TextSpan` objects to `SuperTextSpan` objects.
    */
  implicit def superTextSpan(textSpan: TextSpan) = new SuperTextSpan(textSpan)

  /**
    * A no-arg constructor that uses [[SillySentenceSplitter.SillyPattern]] as the sentence splitting `Regex`.
    */
  def this() = this(SillySentenceSplitter.SillyPattern)
  
  /**
    * This class uses the constructor-provided `Regex` to split the sections of the provided `Communication` object.
    * 
    * @param comm The communication to use for sentence splitting.
    * @return A `SentenceSegmentationCollection` object with split `Sentence` objects.
    */
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
  
  /**
    * A function that takes a `String` and returns a `List[Sentence]`
    * that split the text given the class's `Regex`.
    * 
    * @param text The text to split into `Sentence`s.
    * @return a `List` of `Sentence` objects.
    */
  def createSentenceArrayFromRegex(text: String) : List[Sentence] = {
    (this.pattern findAllMatchIn text) map { sentence =>
      val ts = new TextSpan()
        .setStart(sentence.start)
        .setEnding(sentence.end)
      new Sentence().setUuid(ConcreteUtil.generateUUID).setTextSpan(ts)
    } toList
  }
}

/**
  * Constants associated with the [[SillySentenceSplitter]] class.
  */
object SillySentenceSplitter {
  /**
    * A `Regex` constant used if no `Regex` object is passed in to the [[SillySentenceSplitter]] constructor.
    * {{{
    * "[a-zA-Z0-9 ']+[.?!]+".r
    * }}} 
    */
  val SillyPattern : Regex = "[a-zA-Z0-9 ']+[.?!]+".r

  /**
    * Return the `AnnotationMetadata` for this tool (a constant value).
    */
  val Metadata : AnnotationMetadata = new AnnotationMetadata()
                                            .setConfidence(1)
                                            .setTool("SillySentenceSplitter v1.0s")
                                            .setTimestamp(System.currentTimeMillis() / 1000)
}
