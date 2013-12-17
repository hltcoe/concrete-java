/**
 * 
 */
package concrete.examples.java;

import java.util.List;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationCollection;
import edu.jhu.hlt.tift.Tokenizer;

/**
 * Leverage Tift to create a (default: whitespaced-tokenized) {@link Tokenization} for all {@link Sentence}s in a {@link Communication} object.
 * 
 * @author max
 * 
 */
public class RebarTokenizer {
  private final Tokenizer tokenizer;
  
  public RebarTokenizer () {
    this.tokenizer = Tokenizer.WHITESPACE;
  }
  
  public RebarTokenizer(Tokenizer t) {
    this.tokenizer = t;
  }

  /**
   * Generate an {@link AnnotationMetadata} object that describes this "tool".
   * 
   * @return
   */
  private AnnotationMetadata getMetadata() {
    AnnotationMetadata md = new AnnotationMetadata();
    md.confidence = 1.0d;
    md.timestamp = (int) (System.currentTimeMillis() / 1000);
    md.tool = "Tift v1.0.5-SNAPSHOT-" + this.tokenizer.toString();
    return md;
  }
  
  public TokenizationCollection tokenize(Communication c) {
    TokenizationCollection tc = new TokenizationCollection();
    tc.metadata = this.getMetadata();
    
    List<Section> sections = c.getSectionSegmentation().getSectionList();
    for (Section sc : sections) {
      List<Sentence> sentList = sc.getSentenceSegmentation().getSentenceList();
      for (Sentence s : sentList) {
        TextSpan ts = s.getTextSpan();
        String sentText = c.getText().substring(ts.getStart(), ts.getEnding());
        Tokenization tok = this.tokenizer.tokenizeToConcrete(sentText, 0);
        tok.setSentenceId(s.getUuid());
        tc.addToTokenizationList(tok);
      }
    }
    
    return tc;
  }
}
