import collection.mutable.Stack
import org.scalatest._
import edu.jhu.hlt.concrete.EntityMentionSet
import edu.jhu.hlt.concrete.Communication
import scala.collection.JavaConversions._
import com.typesafe.scalalogging.slf4j.Logging

class ExampleSpec extends FlatSpec with Matchers with Logging {

  def tagEntityMentions (comm : Communication) : EntityMentionSet = {
    val ems = new EntityMentionSet
    val commText = comm.getText()
    comm.getSectionSegmentation().getSectionList().par.map { section =>
      section.getSentenceSegmentation().getSentenceList().map { sentence =>
        sentence.getTokenization().getTokenList().map { token =>
          val ts = token.getTextSpan()
          val tokenText = commText.substring(ts.getStart(), ts.getEnding())
          logger.info("Got a token: " + tokenText)
        }
      }
    }
    
    ems
  }
  
  "The EntityMentionTagger" should "print stuff" in {
    val es = new ExampleSpec
    //val testComm = new Communication().set
//    val stack = new Stack[Int]
//    stack.push(1)
//    stack.push(2)
//    stack.pop() should be (2)
//    stack.pop() should be (1)
  }

//  it should "throw NoSuchElementException if an empty stack is popped" in {
//    val emptyStack = new Stack[Int]
//    a [NoSuchElementException] should be thrownBy {
//      emptyStack.pop()
//    } 
//  }
}