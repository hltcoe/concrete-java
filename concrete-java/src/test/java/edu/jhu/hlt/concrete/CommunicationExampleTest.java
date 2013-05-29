/**
 * 
 */
package edu.jhu.hlt.concrete;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.Section;
import edu.jhu.hlt.concrete.Concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Concrete.Sentence;
import edu.jhu.hlt.concrete.Concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Concrete.TextSpan;
import edu.jhu.hlt.concrete.Concrete.Token;
import edu.jhu.hlt.concrete.Concrete.TokenTagging;
import edu.jhu.hlt.concrete.Concrete.TokenTagging.TaggedToken;
import edu.jhu.hlt.concrete.Concrete.Tokenization;
import edu.jhu.hlt.concrete.util.ProtoFactory;

/**
 * @author max
 *
 */
public class CommunicationExampleTest {

    Communication comm;
    final String docText = "Document title. Document text.";
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Communication mockComm = new ProtoFactory().generateMockCommunication();
        Communication.Builder cb = mockComm.toBuilder()
                .setText(docText);
        this.comm = cb.build();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetText() {
        assertEquals(docText, comm.getText());
    }
    
    @Test
    public void testGetDetailedInfo() {
        String documentText = comm.getText();
        
        List<SectionSegmentation> sectionSegList = comm.getSectionSegmentationList();
        for (SectionSegmentation ss : sectionSegList) {
            List<Section> secList = ss.getSectionList();
            
            for (Section sec : secList) {
                List<SentenceSegmentation> sentSegList = sec.getSentenceSegmentationList();
                
                for (SentenceSegmentation sentSeg : sentSegList) { 
                    List<Sentence> sentList = sentSeg.getSentenceList();
                    
                    for (Sentence sent : sentList) {
                        // get the TextSpan (start and end) of this sentence.
                        if (sent.hasTextSpan()) {
                            TextSpan ts = sent.getTextSpan();
                            int sentStart = ts.getStart();
                            int sentEnd = ts.getStart();
                        }
                        
                        List<Tokenization> tokenizationList = 
                                sent.getTokenizationList();
                        
                        for (Tokenization tokenization : tokenizationList) {
                            List<Token> tokens = tokenization.getTokenList();
                            for (Token tok : tokens) {
                                int tokenId = tok.getTokenId();
                                String tokenText = tok.getText();
                                if (tok.hasTextSpan()) {
                                    TextSpan ts = sent.getTextSpan();
                                    int tokenStart = ts.getStart();
                                    int tokenEnd = ts.getStart();
                                }
                            }
                            
                            List<TokenTagging> posTags = 
                                    tokenization.getPosTagsList();
                            for (TokenTagging tt : posTags) {
                                List<TaggedToken> tagTokenList = 
                                        tt.getTaggedTokenList();
                                for (TaggedToken tagTok : tagTokenList) {
                                    int taggedTokenId = tagTok.getTokenId();
                                    String tag = tagTok.getTag();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
