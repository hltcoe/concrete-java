/**
 *
 */
package concrete.examples.scala

import org.scalatest.Matchers
import com.typesafe.scalalogging.slf4j.Logging
import org.scalatest.FlatSpec
import edu.jhu.hlt.concrete.{Communication, CommunicationType, TextSpan}


/**
 * @author max
 *
 */
class SentenceSplitterSpec extends FlatSpec with Matchers with Logging {
  
  "Sentence Splitter" should
    "create three proper sentences" in {
      val text = "hello world! this is only a sample. here's an entity Joe?";
      
      val splitter = new SillySentenceSplitter()
      val sentList = splitter.createSentenceArrayFromRegex(text)
      sentList.size should be (3);
      val fi = sentList.head
      fi.textSpan should be (new TextSpan(0, 12))
    }
}
