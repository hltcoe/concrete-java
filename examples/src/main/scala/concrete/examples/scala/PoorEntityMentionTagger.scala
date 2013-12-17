/**
 *
 */
package concrete.examples.scala

import edu.jhu.hlt.concrete.AnnotationMetadata
import edu.jhu.hlt.concrete.Communication
import edu.jhu.hlt.concrete.EntityMentionSet
import scala.collection.JavaConversions._
import edu.jhu.hlt.concrete.EntityMention

import edu.jhu.hlt.concrete.EntityType
import edu.jhu.hlt.concrete.TokenRefSequence
import scala.collection.mutable.ListBuffer
import edu.jhu.hlt.concrete.Tokenization
import scala.collection.mutable.Buffer

import edu.jhu.hlt.concrete.util.ConcreteUtil

/**
 * A Scala class that attempts to generate EntityMentions based on tokens that begin with a capital letter.
 *
 * @author max
 *
 */
object PoorEntityMentionTagger {
  val Metadata: AnnotationMetadata = {
    new AnnotationMetadata().setConfidence(1).setTool("PoorEntityMentionTagger v1.0").setTimestamp(System.currentTimeMillis() / 1000)
  }

  def tagEntityMentions(comm: Communication): EntityMentionSet = {
    val ems = new EntityMentionSet
    val commText = comm.getText()
    comm.getSectionSegmentation().getSectionList().par.map { section =>
      section.getSentenceSegmentation().getSentenceList().map { sentence =>
        val tkz = sentence.getTokenization()
        tkz.getTokenList().map { token =>
          val ts = token.getTextSpan()
          val tokenText = commText.substring(ts.getStart(), ts.getEnding())
          if (tokenText.charAt(0).isUpper) {
            val tkIdxList = new java.util.ArrayList[Integer]
            tkIdxList.add(token.getTokenIndex())
            
            val trs = new TokenRefSequence()
              .setTextSpan(ts)
              .setTokenizationId(tkz.getUuid())
              .setTokenIndexList(tkIdxList)
            val em = new EntityMention()
              .setUuid(ConcreteUtil.generateUUID)
              .setText(tokenText)
              .setEntityType(EntityType.PERSON)
              .setTokens(trs)
              
            ems.addToMentionSet(em)
          }
        }
      }
    }
    
    ems
      .setUuid(ConcreteUtil generateUUID)
      .setMetadata(this.Metadata)
  }
  
  def tagCapitalizedWordsAsPeopleEntityMentions (commText: String, tkz : Tokenization) : List[EntityMention] = {
    val tokenTextSpanTextThreeTuple = tkz.getTokenList.map { token =>
      val ts = token.getTextSpan()
      (token, ts, commText.substring(ts.getStart(), ts.getEnding()))
    }
    
    val toTag = tokenTextSpanTextThreeTuple.filter { tuple =>
      tuple._3.charAt(0).isUpper
    }
    
    toTag.map { tuple =>
      val token = tuple._1
      val ts = tuple._2
      val tokenText = tuple._3
      
      val tkIdxList = new java.util.ArrayList[Integer]
      tkIdxList.add(token.getTokenIndex())
      
      val trs = new TokenRefSequence()
          .setTextSpan(ts)
          .setTokenizationId(tkz.getUuid())
          .setTokenIndexList(tkIdxList)
      new EntityMention()
        .setUuid(ConcreteUtil.generateUUID)
        .setText(tokenText)
        .setEntityType(EntityType.PERSON)
        .setTokens(trs)
    } toList
  }
}
