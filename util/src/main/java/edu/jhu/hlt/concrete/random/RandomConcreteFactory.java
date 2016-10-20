/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.random;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationTagging;
import edu.jhu.hlt.concrete.LanguageIdentification;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadataFactory;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * Class that allows for construction of random (in the sense of randomness)
 * Concrete objects.
 */
public class RandomConcreteFactory {

  private final Random r;
  private static final String[] COMM_TYPES = new String[] { "Document", "Tweet", "Other" };
  private static final int COMM_TYPE_SIZE = COMM_TYPES.length;

  public RandomConcreteFactory() {
    this.r = new Random();
  }

  public RandomConcreteFactory(long seed) {
    this.r = new Random(seed);
  }

  public final String communicationType() {
    return COMM_TYPES[this.r.nextInt(COMM_TYPE_SIZE)];
  }

  /**
   * @return a {@link Communication} with all required fields set
   * and a random <code>type</code>
   */
  public Communication communication() {
    return new Communication().setUuid(UUIDFactory.newUUID())
        .setId("corpus_" + this.r.nextInt(Integer.MAX_VALUE))
        .setText("Some sample text.")
        .setType(this.communicationType())
        .setMetadata(AnnotationMetadataFactory.fromCurrentLocalTime()
            .setTool("RandomConcreteFactory"));
  }

  public Communication tweet() {
    Communication c = new Communication();
    String txt = "foo @bar http://foo.com hello! #test cool";
    String maxLen = Integer.toString(Integer.MAX_VALUE);
    String nextId = Integer.toString(r.nextInt(Integer.MAX_VALUE));
    final int maxLenLen = maxLen.length();
    final int nextLen = nextId.length();
    if (nextLen < maxLenLen) {
      int diff = maxLenLen - nextLen;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < diff; i++) {
        sb.append("0");
      }

      nextId = sb.toString() + nextId;
    }

    c.setId(nextId)
        .setUuid(new UUID(java.util.UUID.randomUUID().toString()))
        .setText(txt)
        .setType("tweet");
    AnnotationMetadata amd = new AnnotationMetadata()
        .setTimestamp(Timing.currentLocalTime())
        .setTool("Faker Comm Tagger");
    c.setMetadata(amd.setTool("Fake ingester"));

    CommunicationTagging ct = new CommunicationTagging();
    ct.setUuid(new UUID(java.util.UUID.randomUUID().toString()));
    ct.setTaggingType("bar");
    ct.setMetadata(amd);
    ct.addToConfidenceList(1.0d);
    ct.addToTagList("quxbaz");
    c.addToCommunicationTaggingList(ct);

    LanguageIdentification lid = new LanguageIdentification();
    lid.setUuid(new UUID(java.util.UUID.randomUUID().toString()));
    lid.setMetadata(new AnnotationMetadata(amd).setTool("Twitter LID"));
    lid.putToLanguageToProbabilityMap("eng", this.r.nextDouble());
    c.addToLidList(lid);
    return c;
  }

  /**
   * @param nMembers the amount of items to generate
   * @return a {@link Set} of {@link Communication} objects with all required fields set
   */
  public Set<Communication> communicationSet(int nMembers) {
    Set<Communication> cSet = new HashSet<>(nMembers + 1);
    for (int i = 0; i < nMembers; i++)
      cSet.add(this.communication());
    // could get some dupes
    while (cSet.size() < nMembers)
      cSet.add(this.communication());
    return cSet;
  }

  /**
   *
   * @return a {@link Communication} with a single {@link Section} of type
   * <code>passage</code>
   *
   * @see #communication()
   */
  public Communication withSection() {
	  try {
      final Communication comm = this.communication();
      final TextSpan ts = new TextSpan(0, comm.getText().length());
      AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory(comm);
      AnalyticUUIDGenerator gen = f.create();
      final Section s = new SectionFactory(gen).fromTextSpan(ts, "passage");
      comm.addToSectionList(s);
      return comm;
    } catch (ConcreteException e) {
      throw new RuntimeException(e);
    }
  }
}
