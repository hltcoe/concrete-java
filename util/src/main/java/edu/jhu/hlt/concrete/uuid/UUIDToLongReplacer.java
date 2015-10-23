/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.uuid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TheoryDependencies;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;

/**
 * A utility for replacing Concrete UUIDs with longs.
 * <br>
 * <br>
 * This is nasty, hard coded and brittle. One could reflect
 * to get all UUID fields, but that isn't ideal, as those
 * fields might be in parents who are <code>null</code>,
 * leading to a lot of headaches.
 */
public class UUIDToLongReplacer {

  // The necessary ID element. Must be atomic.
  private final AtomicLong al;
  // For statistics.
  private final LongAccumulator nTDUUIDs;

  public UUIDToLongReplacer() {
    this.al = new AtomicLong();

    this.nTDUUIDs = new LongAccumulator((a, b) -> a + b, 0L);
  }

  /**
   * Utility class that neatly wraps all the junk
   * involving getting and setting UUID strings,
   * managing the underlying map, etc.
   */
  private static final class UUIDStringLongMapWrapper {
    private final Map<String, Long> uuidToLongMap;
    private final AtomicLong alptr;
    // For statistics.
    private final LongAccumulator numTDUUIDs;

    public UUIDStringLongMapWrapper(final AtomicLong alptr) {
      this.uuidToLongMap = new HashMap<>();
      this.alptr = alptr;
      this.numTDUUIDs = new LongAccumulator((a, b) -> a + b, 0L);
    }

    private long evaluateUUID(final UUID uuid) {
      String uuidstr = uuid.getUuidString();
      if (this.uuidToLongMap.containsKey(uuidstr))
        return this.uuidToLongMap.get(uuidstr);
      else {
        long nLong = this.alptr.incrementAndGet();
        this.uuidToLongMap.put(uuidstr, nLong);
        return nLong;
      }
    }

    /**
     * @return the total number of replaced theory dependency
     * {@link UUID}s
     */
    public long getNTDUUIDs() {
      return this.numTDUUIDs.get();
    }

    private String wrapEvaluateUUID(final UUID uuid) {
      return Long.toString(this.evaluateUUID(uuid));
    }

    /**
     * Replace a {@link UUID} string in place with the proper
     * new long from the wrapped {@link AtomicLong} class.
     *
     * @param uuid the UUID to replace
     */
    public void updateUUIDInPlace(final UUID uuid) {
      String nid = this.wrapEvaluateUUID(uuid);
      uuid.setUuidString(nid);
    }

    private void replaceUUIDsWithCtr(List<UUID> uuidList) {
      for (UUID uuid : uuidList) {
        this.updateUUIDInPlace(uuid);
        numTDUUIDs.accumulate(1L);
      }
    }

    /**
     * Wrapper for neatly replacing {@link TheoryDependencies} {@link UUID}s
     * inside {@link AnnotationMetadata} objects.
     *
     * @param md the object to replace all UUIDs in
     */
    public void updateMetadata(final AnnotationMetadata md) {
      this.updateTheoryDependencies(md.getDependencies());
    }

    /**
     * Nasty method that replaces all {@link TheoryDependencies} {@link UUID}s.
     *
     * @param td the object whose UUIDs to replace
     */
    public void updateTheoryDependencies(final TheoryDependencies td) {
      // ....
      // 1.
      if (td.isSetSectionTheoryList())
        this.replaceUUIDsWithCtr(td.getSectionTheoryList());
      // 2.
      if (td.isSetSentenceTheoryList())
        this.replaceUUIDsWithCtr(td.getSentenceTheoryList());
      // 3.
      if (td.isSetTokenizationTheoryList())
        this.replaceUUIDsWithCtr(td.getTokenizationTheoryList());
      // 4.
      if (td.isSetPosTagTheoryList())
        this.replaceUUIDsWithCtr(td.getPosTagTheoryList());
      // 5.
      if (td.isSetNerTagTheoryList())
        this.replaceUUIDsWithCtr(td.getNerTagTheoryList());
      // 6.
      if (td.isSetLemmaTheoryList())
        this.replaceUUIDsWithCtr(td.getLemmaTheoryList());
      // 7.
      if (td.isSetLangIdTheoryList())
        this.replaceUUIDsWithCtr(td.getLangIdTheoryList());
      // 8.
      if (td.isSetParseTheoryList())
        this.replaceUUIDsWithCtr(td.getParseTheoryList());
      // 9.
      if (td.isSetDependencyParseTheoryList())
        this.replaceUUIDsWithCtr(td.getDependencyParseTheoryList());
      // 10.
      if (td.isSetTokenAnnotationTheoryList())
        this.replaceUUIDsWithCtr(td.getTokenAnnotationTheoryList());
      // 11.
      if (td.isSetEntityMentionSetTheoryList())
        this.replaceUUIDsWithCtr(td.getEntityMentionSetTheoryList());
      // 12.
      if (td.isSetEntitySetTheoryList())
        this.replaceUUIDsWithCtr(td.getEntitySetTheoryList());
      // 13.
      if (td.isSetSituationMentionSetTheoryList())
        this.replaceUUIDsWithCtr(td.getSituationMentionSetTheoryList());
      // 14.
      if (td.isSetSituationSetTheoryList())
        this.replaceUUIDsWithCtr(td.getSituationSetTheoryList());
      // 15.
      if (td.isSetCommunicationsList())
        this.replaceUUIDsWithCtr(td.getCommunicationsList());
    }
  }

  /**
   * Method that mutates a given {@link Communication} object,
   * replacing all stanford-placed {@link UUID} objects.
   * TODO: update with Situations, etc. that stanford does not generate.
   *
   * @param c the {@link Communication} whose {@link UUID}s will be replaced
   */
  private void replaceAllUUIDsWithAutoIncrementLong(final Communication c) {
    UUIDStringLongMapWrapper wrapper = new UUIDStringLongMapWrapper(this.al);

    wrapper.updateUUIDInPlace(c.getUuid());

    if (c.isSetSectionList()) {
      for (Section s : c.getSectionList()) {
        wrapper.updateUUIDInPlace(s.getUuid());

        if (s.isSetSentenceList()) {
          for (Sentence st : s.getSentenceList()) {
            wrapper.updateUUIDInPlace(st.getUuid());

            if (st.isSetTokenization()) {
              Tokenization tkz = st.getTokenization();
              wrapper.updateMetadata(tkz.getMetadata());
              wrapper.updateUUIDInPlace(tkz.getUuid());

              if (tkz.isSetTokenTaggingList()) {
                for (TokenTagging tt : tkz.getTokenTaggingList()) {
                  wrapper.updateMetadata(tt.getMetadata());
                  wrapper.updateUUIDInPlace(tt.getUuid());
                }
              }

              if (tkz.isSetDependencyParseList()) {
                for (DependencyParse depp : tkz.getDependencyParseList()) {
                  wrapper.updateUUIDInPlace(depp.getUuid());
                  wrapper.updateMetadata(depp.getMetadata());
                }
              }

              if (tkz.isSetParseList()) {
                for (Parse p : tkz.getParseList()) {
                  wrapper.updateUUIDInPlace(p.getUuid());
                  wrapper.updateMetadata(p.getMetadata());
                }
              }
            }
          }
        }
      }
    }

    if (c.isSetEntityMentionSetList()) {
      for (EntityMentionSet ems : c.getEntityMentionSetList()) {
        wrapper.updateUUIDInPlace(ems.getUuid());
        wrapper.updateMetadata(ems.getMetadata());

        if (ems.isSetMentionList()) {
          for (EntityMention em : ems.getMentionList()) {
            wrapper.updateUUIDInPlace(em.getUuid());
            if (em.isSetTokens())
              wrapper.updateUUIDInPlace(em.getTokens().getTokenizationId());
          }
        }
      }
    }

    if (c.isSetEntitySetList()) {
      for (EntitySet es : c.getEntitySetList()) {
        wrapper.updateUUIDInPlace(es.getUuid());
        wrapper.updateMetadata(es.getMetadata());

        if (es.isSetMentionSetId())
          wrapper.updateUUIDInPlace(es.getMentionSetId());

        if (es.isSetEntityList()) {
          for (Entity e : es.getEntityList()) {
            wrapper.updateUUIDInPlace(e.getUuid());
            if (e.isSetMentionIdList())
              for (UUID i : e.getMentionIdList())
                wrapper.updateUUIDInPlace(i);

          }
        }
      }
    }

    // TODO: situations, etc.
    // not done in stanford, but should be done eventually.

    // update the total # of theory dependency UUIDs
    // for data gathering's sake
    this.nTDUUIDs.accumulate(wrapper.getNTDUUIDs());
  }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
