/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.random;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadataFactory;
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

  public Communication communication() {
    return new Communication()
      .setUuid(UUIDFactory.newUUID())
      .setId("corpus_" + this.r.nextInt(Integer.MAX_VALUE))
      .setText("Some sample text.")
      .setType(this.communicationType())
      .setMetadata(AnnotationMetadataFactory.fromCurrentLocalTime()
          .setTool("RandomConcreteFactory"));
  }

  public Set<Communication> communicationSet(int nMembers) {
    Set<Communication> cSet = new HashSet<>(nMembers + 1);
    for (int i = 0; i < nMembers; i++)
      cSet.add(this.communication());
    // could get some dupes
    while (cSet.size() < nMembers)
      cSet.add(this.communication());
    return cSet;
  }
}
