package edu.jhu.hlt.concrete.miscommunication;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import edu.jhu.hlt.concrete.miscommunication.MiscSection;
import edu.jhu.hlt.concrete.miscommunication.MiscSentence;
import edu.jhu.hlt.concrete.miscommunication.NonEmptyString;

public class MiscSectionTest {

  @Test
  public void test() {
    final String commID = "foo";
    final NonEmptyString cid = NonEmptyString.create(commID);
    UUID stid = UUID.randomUUID();
    MiscSentence msent = new MiscSentence.Builder()
        .setUUID(stid)
        .setCommunicationID(cid)
        .setCommunicationID(NonEmptyString.create(commID))
        .build();

    MiscSection.Builder msb = new MiscSection.Builder()
        .setUUID(UUID.randomUUID())
        .setKind("news")
        .setCommunicationID(cid);
    msb.putIdToSentenceMap(msent.getUUID(), msent);
    msb.build();
    assertTrue(msb.getIdToSentenceMap().keySet().contains(stid));
  }
}
