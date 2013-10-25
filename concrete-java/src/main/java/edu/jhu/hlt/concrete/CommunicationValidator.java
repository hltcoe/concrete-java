package edu.jhu.hlt.concrete;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.Entity;
import edu.jhu.hlt.concrete.Concrete.EntityMention;
import edu.jhu.hlt.concrete.Concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.Concrete.EntitySet;
import edu.jhu.hlt.concrete.Concrete.Section;
import edu.jhu.hlt.concrete.Concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Concrete.Sentence;
import edu.jhu.hlt.concrete.Concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Concrete.Situation;
import edu.jhu.hlt.concrete.Concrete.Situation.Justification;
import edu.jhu.hlt.concrete.Concrete.SituationMention;
import edu.jhu.hlt.concrete.Concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.Concrete.SituationSet;
import edu.jhu.hlt.concrete.Concrete.Tokenization;
import edu.jhu.hlt.concrete.Concrete.UUID;

/**
 * This class validates communications by ensuring that all UUID pointers are valid.
 * 
 * @author dkolas
 */
public class CommunicationValidator {

  private static int checkCount = 0;

  /**
   * @param args
   */
  public static void main(String[] args) throws IOException {
    BufferedInputStream input = new BufferedInputStream(new FileInputStream(args[0]));
    Writer errorWriter = new OutputStreamWriter(System.out);

    int c = 0;
    while (input.available() != 0) {
      Communication communication = Communication.parseDelimitedFrom(input);
      System.out.println("Validating #" + (c++) + ": " + communication.getGuid());
      boolean result = validateCommunication(communication, errorWriter);
      System.out.println(result ? "Valid." : "INVALID");
      System.out.println(checkCount);
    }
    input.close();
  }

  public static boolean validateCommunication(Communication communication, Writer errorWriter) throws IOException {

    boolean valid = true;

    // FIrst, read in all of the valid UUIDs for the things that can be referenced
    Set<UUID> tokenizationIds = new HashSet<UUID>();
    Set<UUID> entityMentionIds = new HashSet<UUID>();
    Set<UUID> situationMentionIds = new HashSet<UUID>();
    Set<UUID> entityIds = new HashSet<UUID>();
    Set<UUID> situationIds = new HashSet<UUID>();

    // Read in tokenization UUIDs
    for (SectionSegmentation segmentation : communication.getSectionSegmentationList()) {
      for (Section section : segmentation.getSectionList()) {
        for (SentenceSegmentation sentenceSegmentation : section.getSentenceSegmentationList()) {
          for (Sentence sentence : sentenceSegmentation.getSentenceList()) {
            for (Tokenization tokenization : sentence.getTokenizationList()) {
              tokenizationIds.add(tokenization.getUuid());
            }
          }
        }
      }
    }

    // Read in entity mention UUIDs
    for (EntityMentionSet entityMentionSet : communication.getEntityMentionSetList()) {
      for (EntityMention mention : entityMentionSet.getMentionList()) {
        entityMentionIds.add(mention.getUuid());
      }
    }

    // Read in entity UUIDs
    for (EntitySet entitySet : communication.getEntitySetList()) {
      for (Entity entity : entitySet.getEntityList()) {
        entityIds.add(entity.getUuid());
      }
    }

    // Read in SituationMention UUIDs
    for (SituationMentionSet situationMentionSet : communication.getSituationMentionSetList()) {
      for (SituationMention mention : situationMentionSet.getMentionList()) {
        situationMentionIds.add(mention.getUuid());
      }
    }

    // Read in Situation UUIDs
    for (SituationSet situationSet : communication.getSituationSetList()) {
      for (Situation situation : situationSet.getSituationList()) {
        situationIds.add(situation.getUuid());
      }
    }

    // Now traverse the communication and verify!
    for (EntityMentionSet entityMentionSet : communication.getEntityMentionSetList()) {
      for (EntityMention mention : entityMentionSet.getMentionList()) {
        if (mention.hasTokens() && mention.getTokens().hasTokenizationId()) {
          valid &= check(tokenizationIds, mention.getTokens().getTokenizationId(), "Tokenization", errorWriter);
        }
      }
    }

    for (EntitySet entitySet : communication.getEntitySetList()) {
      for (Entity entity : entitySet.getEntityList()) {
        for (UUID entityMention : entity.getMentionIdList()) {
          valid &= check(entityMentionIds, entityMention, "EntityMention", errorWriter);
        }
      }
    }

    // Read in SituationMention UUIDs
    for (SituationMentionSet situationMentionSet : communication.getSituationMentionSetList()) {
      for (SituationMention mention : situationMentionSet.getMentionList()) {
        valid &= check(tokenizationIds, mention.getTokens().getTokenizationId(), "Tokenization", errorWriter);
        for (SituationMention.Argument arg : mention.getArgumentList()) {
          if (arg.hasEntityMentionId()) {
            valid &= check(entityMentionIds, arg.getEntityMentionId(), "EntityMention", errorWriter);
          } else if (arg.hasSituationMentionId()) {
            valid &= check(situationMentionIds, arg.getSituationMentionId(), "SituationMention", errorWriter);
          }
        }
      }
    }

    // Read in Situation UUIDs
    for (SituationSet situationSet : communication.getSituationSetList()) {
      for (Situation situation : situationSet.getSituationList()) {
        for (Situation.Argument arg : situation.getArgumentList()) {
          if (arg.hasEntityId()) {
            valid &= check(entityIds, arg.getEntityId(), "Entity", errorWriter);
          } else if (arg.hasSituationId()) {
            valid &= check(situationIds, arg.getSituationId(), "Situation", errorWriter);
          }
        }
        for (Justification just : situation.getJustificationList()) {
          if (just.hasMention()) {
            valid &= check(situationMentionIds, just.getMention(), "SituationMention", errorWriter);
          }
        }
      }
    }

    return valid;
  }

  private static boolean check(Set<UUID> uuids, UUID uuid, String type, Writer writer) throws IOException {
    checkCount++;
    if (!uuids.contains(uuid)) {
      if (writer != null) {
        writer.write("Could not find " + type + " UUID: " + uuid);
      }
      return false;
    } else {
      return true;
    }
  }
}
