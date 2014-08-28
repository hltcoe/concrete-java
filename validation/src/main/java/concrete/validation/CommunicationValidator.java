/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package concrete.validation;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import concrete.util.concurrent.ConcurrentCommunicationLoader;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.validation.ValidatableEntityMentionSet;
import edu.jhu.hlt.concrete.validation.ValidatableSectionSegmentation;
import edu.jhu.hlt.concrete.validation.ValidatableSentenceSegmentation;
import edu.jhu.hlt.concrete.validation.ValidatableTokenization;

/**
 * @author max
 *
 */
public class CommunicationValidator {
  
  private static final Logger logger = LoggerFactory.getLogger(CommunicationValidator.class);
  
  private Communication comm;
  
  /**
   * 
   */
  public CommunicationValidator(Communication comm) {
    this.comm = comm;
  }
  
  public boolean validate() {
    boolean valid = true;
    if (this.comm.isSetSectionSegmentationList()) {
      Iterator<SectionSegmentation> ssIter = this.comm.getSectionSegmentationListIterator();
      while (valid && ssIter.hasNext()) {
        SectionSegmentation ss = ssIter.next();
        valid = new ValidatableSectionSegmentation(ss).validate(this.comm);
        if (valid) {
          for (Section sect : ss.getSectionList()) {
            if (sect.isSetSentenceSegmentationList()) {
              Iterator<SentenceSegmentation> sentSegIter = sect.getSentenceSegmentationListIterator();
              while (valid && sentSegIter.hasNext()) {
                SentenceSegmentation sentSeg = sentSegIter.next();
                valid = new ValidatableSentenceSegmentation(sentSeg).validate(this.comm);
                if (valid) {
                  for (Sentence sent : sentSeg.getSentenceList()) {
                    if (sent.isSetTokenizationList()) {
                      Iterator<Tokenization> tokIter = sent.getTokenizationListIterator();
                      while (valid && tokIter.hasNext()) {
                        Tokenization tok = tokIter.next();
                        valid = new ValidatableTokenization(tok).validate(this.comm);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    
    if (this.comm.isSetEntityMentionSetList()) {
      Iterator<EntityMentionSet> emsIter = this.comm.getEntityMentionSetListIterator();
      while (valid && emsIter.hasNext()) {
        EntityMentionSet ems = emsIter.next();
        valid = new ValidatableEntityMentionSet(ems).validate(this.comm);
      }
    }
    
    return valid;  
  }

  /**
   * @param args
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub
    if (args.length != 1) {
      System.out.println("This program takes a text file containing a communication per line and validates each of them.");
      System.out.println("Usage: " + CommunicationValidator.class.getName() + " <path/to/list/of/comm/files>");
      System.exit(1);
    }
    
    try (ConcurrentCommunicationLoader ccl = new ConcurrentCommunicationLoader();) {
      List<Future<Communication>> commList = ccl.bulkLoad(args[0]);
      for (Future<Communication> c : commList) {
        Communication comm = c.get();
        String result = new CommunicationValidator(comm).validate() ? "VALID." : "INVALID.";
        logger.info("Communication: " + comm.getId() + " is " + result);
      }
    }
  }
}
