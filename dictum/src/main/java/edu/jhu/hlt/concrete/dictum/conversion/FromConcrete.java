/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum.conversion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.LanguageIdentification;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.dictum.Communication;
import edu.jhu.hlt.concrete.dictum.CommunicationTagging;
import edu.jhu.hlt.concrete.dictum.Constituent;
import edu.jhu.hlt.concrete.dictum.Dependency;
import edu.jhu.hlt.concrete.dictum.DependencyParse;
import edu.jhu.hlt.concrete.dictum.FlatTextSpan;
import edu.jhu.hlt.concrete.dictum.InDocEntity;
import edu.jhu.hlt.concrete.dictum.InDocEntityGroup;
import edu.jhu.hlt.concrete.dictum.InDocEntityMention;
import edu.jhu.hlt.concrete.dictum.InDocEntityMentionGroup;
import edu.jhu.hlt.concrete.dictum.LanguageID;
import edu.jhu.hlt.concrete.dictum.Parse;
import edu.jhu.hlt.concrete.dictum.Section;
import edu.jhu.hlt.concrete.dictum.Sentence;
import edu.jhu.hlt.concrete.dictum.SpanLink;
import edu.jhu.hlt.concrete.dictum.TaggedToken;
import edu.jhu.hlt.concrete.dictum.TaggedTokenGroup;
import edu.jhu.hlt.concrete.dictum.Token;
import edu.jhu.hlt.concrete.dictum.Tokenization;
import edu.jhu.hlt.concrete.dictum.primitives.Confidence;
import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.IntZeroOrGreater;
import edu.jhu.hlt.concrete.dictum.primitives.NonEmptyNonWhitespaceString;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;
/**
 * Utility class that takes in Concrete {@link edu.jhu.hlt.concrete.Communication}
 * objects and converts them to Dictum {@link Communication} objects.
 */
public final class FromConcrete {

  private static final Logger LOGGER = LoggerFactory.getLogger(FromConcrete.class);

  private FromConcrete() {
  }

  /**
   * @param c
   *          a Concrete {@link edu.jhu.hlt.concrete.Communication}
   * @return a dictum {@link Communication}
   * @throws InvalidStructException
   *           on failed validation
   */
  public static final Communication convert(edu.jhu.hlt.concrete.Communication c) throws InvalidStructException {
    Communication.Builder b = new Communication.Builder();
    try {
      b.setId(c.getId())
          .setUUID(UUID.fromString(c.getUuid().getUuidString()))
          .setType(c.getType())
          .setText(c.getText());
      if (c.isSetStartTime())
        b.setStartTime(UnixTimestamp.create(c.getStartTime()));
      if (c.isSetEndTime())
        b.setEndTime(UnixTimestamp.create(c.getEndTime()));
      AnnotationMetadata amd = c.getMetadata();
      b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
        .setKBest(IntGreaterThanZero.create(amd.getKBest()))
        .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));

      // if (c.isSetCommunicationMetadata()) {
        // CommunicationMetadata cmd = c.getCommunicationMetadata();
        // NITFInfo nitfi = cmd.getNitfInfo();
        // TODO: NITFInfo -> dictum
      // }

      if (c.isSetCommunicationTaggingList())
        c.getCommunicationTaggingList()
            .stream()
            .map(FromConcrete::convertConcreteCommTagging)
            .forEach(b::addTags);
      if (c.isSetKeyValueMap())
        b.putAllKVs(c.getKeyValueMap());
      if (c.isSetLidList())
        for (LanguageIdentification lid : c.getLidList())
          b.addLanguageIDs(convert(lid));
      if (c.isSetSectionList()) {
        for (edu.jhu.hlt.concrete.Section s : c.getSectionList()) {
          Section ps = convert(s);
          b.putIdToSectionMap(ps.getUUID(), ps);
        }
      }

      ImmutableMap.Builder<UUID, Tokenization> mb = new ImmutableMap.Builder<>();
      for (Section s : b.getIdToSectionMap().values()) {
        for (Sentence st : s.getIdToSentenceMap().values()) {
          st.getPowerTokenization().ifPresent(t -> mb.put(t.getUUID(), t));
        }
      }
      Map<UUID, Tokenization> m = mb.build();

      if (c.isSetEntityMentionSetList()) {
        for (EntityMentionSet s : c.getEntityMentionSetList()) {
          InDocEntityMentionGroup pemg = convert(s, m);
          b.putIdToEntityMentionsMap(pemg.getUUID(), pemg);
        }
      }

      if (c.isSetEntitySetList()) {
        for (EntitySet s : c.getEntitySetList()) {
          InDocEntityGroup peg = convert(s);
          b.putIdToEntitiesMap(peg.getUUID(), peg);
        }
      }

      return b.build();
    } catch (NullPointerException | IllegalArgumentException | IllegalStateException e) {
      throw new InvalidStructException("Caught exception converting concrete communication.", e);
    }
  }

  private static final InDocEntityGroup convert(EntitySet a) {
    InDocEntityGroup.Builder b = new InDocEntityGroup.Builder();
    b.setUUID(UUID.fromString(a.getUuid().getUuidString()));
    AnnotationMetadata amd = a.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    for (Entity e : a.getEntityList()) {
      InDocEntity pe = convert(e);
      b.putIdToEntityMap(pe.getUUID(), pe);
    }

    if (a.isSetMentionSetId())
      b.setMentionSetUUID(convert(a.getMentionSetId()));
    return b.build();
  }

  private static final InDocEntity convert(Entity a) {
    InDocEntity.Builder b = new InDocEntity.Builder();
    b.setUUID(convert(a.getUuid()));
    b.setNullableCanonicalName(a.getCanonicalName());
    if (a.isSetConfidence())
      b.setConfidence(new Confidence.Builder().setScore(a.getConfidence()).build());
    b.setNullableType(a.getType());
    for (edu.jhu.hlt.concrete.UUID u : a.getMentionIdList())
      b.addMentionUUIDs(convert(u));

    return b.build();
  }

  private static final InDocEntityMentionGroup convert(EntityMentionSet a, Map<UUID, Tokenization> m) {
    InDocEntityMentionGroup.Builder b = new InDocEntityMentionGroup.Builder();
    b.setUUID(convert(a.getUuid()));
    AnnotationMetadata amd = a.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    for (EntityMention e : a.getMentionList()) {
      InDocEntityMention pem = convert(e, m);
      b.putIdToEntityMentionMap(pem.getUUID(), pem);
    }

    return b.build();
  }

  private static final InDocEntityMention convert(EntityMention a, Map<UUID, Tokenization> m) {
    InDocEntityMention.Builder b = new InDocEntityMention.Builder();
    b.setUUID(convert(a.getUuid()));
    b.setNullableEntityType(a.getEntityType());
    b.setNullablePhraseType(a.getPhraseType());
    b.setNullableText(a.getText());
    if (a.isSetChildMentionIdList())
      for (edu.jhu.hlt.concrete.UUID u : a.getChildMentionIdList())
        b.addChildMentionUUIDs(convert(u));
    if (a.isSetConfidence())
      b.setConfidence(new Confidence.Builder().setScore(a.getConfidence()).build());
    TokenRefSequence trs = a.getTokens();
    UUID u = convert(trs.getTokenizationId());
    if (!m.containsKey(u))
      throw new IllegalStateException("TokenRefSequence references tokenization UUID: " + u.toString()
             + ", but this Tokenization UUID is not present in this communication.");
    Tokenization tkz = m.get(u);
    Map<Integer, Token> toks = tkz.getIndexToTokenMap();
    if (trs.isSetAnchorTokenIndex()) {
      int ai = trs.getAnchorTokenIndex();
      b.setAnchorToken(toks.get(ai));
      b.setAnchorTokenIndex(ai);
    }

    b.setTextSpan(convert(trs.getTextSpan()));
    for (Integer i : trs.getTokenIndexList()) {
      b.addTokenIndices(i);
      Token t = toks.get(i);
      b.addTokens(t);
    }

    b.setTokenizationUUID(u);
    b.setTokenization(tkz);
    return b.build();
  }

  private static final CommunicationTagging convertConcreteCommTagging(edu.jhu.hlt.concrete.CommunicationTagging cct) {
    CommunicationTagging.Builder b = new CommunicationTagging.Builder();
    b.setUUID(UUID.fromString(cct.getUuid().getUuidString()));
    AnnotationMetadata amd = cct.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    b.setTaggingType(cct.getTaggingType());
    b.putAllTagToConfidenceMap(convert(cct.getTagList(), cct.getConfidenceList()));
    return b.build();
  }

  private static final UUID convert(edu.jhu.hlt.concrete.UUID uuid) {
    return UUID.fromString(uuid.getUuidString());
  }

  public static final LanguageID convert(LanguageIdentification lid) {
    LanguageID.Builder b = new LanguageID.Builder();
    b.setUUID(convert(lid.getUuid()));
    AnnotationMetadata amd = lid.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    for (Map.Entry<String, Double> e : lid.getLanguageToProbabilityMap().entrySet()) {
      LOGGER.debug("Adding following to map: {}", e.toString());
      b.putLanguageToProbMap(e.getKey(), Confidence.fromDouble(e.getValue()));
    }

    return b.build();
  }

  private static final Section convert(edu.jhu.hlt.concrete.Section s) {
    Section.Builder b = new Section.Builder();
    b.setUUID(convert(s.getUuid()));
    b.setKind(s.getKind());
    b.setNullableLabel(s.getLabel());
    b.setTextSpan(convert(s.getTextSpan()));

    if (s.isSetNumberList())
      b.addAllNumbers(s.getNumberList());
    if (s.isSetSentenceList())
      for (edu.jhu.hlt.concrete.Sentence st : s.getSentenceList()) {
        Sentence pst = convert(st);
        b.putIdToSentenceMap(pst.getUUID(), pst);
      }

    return b.build();
  }

  private static final Optional<FlatTextSpan> convert(TextSpan ts) {
    if (ts == null)
      return Optional.empty();
    else
      return Optional.of(new FlatTextSpan.Builder()
          .setStart(ts.getStart())
          .setEnd(ts.getEnding())
          .build());
  }

  public static final Sentence convert(edu.jhu.hlt.concrete.Sentence s) {
    Sentence.Builder b = new Sentence.Builder();
    b.setUUID(convert(s.getUuid()));
    b.setTextSpan(convert(s.getTextSpan()));
    if (s.isSetTokenization())
      b.setPowerTokenization(convert(s.getTokenization()));

    return b.build();
  }

  private static final Tokenization convert (edu.jhu.hlt.concrete.Tokenization tkz) {
    Tokenization.Builder b = new Tokenization.Builder();
    b.setUUID(convert(tkz.getUuid()));
    AnnotationMetadata amd = tkz.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    b.setType(tkz.getKind().toString());
    if (tkz.isSetTokenList()) {
      List<edu.jhu.hlt.concrete.Token> tl = tkz.getTokenList().getTokenList();
      for (edu.jhu.hlt.concrete.Token t : tl) {
        Token pt = convert(t);
        b.putIndexToTokenMap(pt.getIndex().getVal(), pt);
      }
    }

    if (tkz.isSetTokenTaggingList()) {
      for (TokenTagging ttl : tkz.getTokenTaggingList()) {
        TaggedTokenGroup ptt = convert(ttl);
        b.putIdToTokenTagGroupMap(ptt.getUUID(), ptt);
      }
    }

    if (tkz.isSetParseList()) {
      for (edu.jhu.hlt.concrete.Parse p : tkz.getParseList()) {
        Parse pp = convert(p);
        b.putIdToParseMap(pp.getUUID(), pp);
      }
    }

    if (tkz.isSetDependencyParseList()) {
      for (edu.jhu.hlt.concrete.DependencyParse dp : tkz.getDependencyParseList()) {
        DependencyParse pdp = convert(dp);
        b.putIdToDependencyParseMap(pdp.getUUID(), pdp);
      }
    }

    Tokenization local = b.build();
    Tokenization.Builder nb = new Tokenization.Builder();
    nb.mergeFrom(local);
    if (tkz.isSetSpanLinkList()) {
      for (edu.jhu.hlt.concrete.SpanLink sl : tkz.getSpanLinkList()) {
        SpanLink psl = convert(sl, local);
        nb.addSpanLinks(psl);
      }
    }

    return nb.build();
  }

  private static final SpanLink convert(edu.jhu.hlt.concrete.SpanLink sl, Tokenization tkz) {
    SpanLink.Builder b = new SpanLink.Builder();
    if (sl.isSetConcreteTarget())
      b.setConcreteTarget(convert(sl.getConcreteTarget()));
    b.setNullableExternalTarget(sl.getExternalTarget());
    TokenRefSequence trs = sl.getTokens();
    Map<Integer, Token> toks = tkz.getIndexToTokenMap();
    if (trs.isSetAnchorTokenIndex()) {
      int ai = trs.getAnchorTokenIndex();
      b.setAnchorToken(toks.get(ai));
      b.setAnchorTokenIndex(ai);
    }

    b.setTextSpan(convert(trs.getTextSpan()));
    for (Integer i : trs.getTokenIndexList()) {
      b.addTokenIndices(i);
      Token t = toks.get(i);
      b.addTokens(t);
    }

    b.setTokenization(tkz);
    b.setLinkType(sl.getLinkType());
    b.setTokenizationUUID(tkz.getUUID());
    return b.build();
  }

  private static final DependencyParse convert(edu.jhu.hlt.concrete.DependencyParse p) {
    DependencyParse.Builder b = new DependencyParse.Builder();
    b.setUUID(convert(p.getUuid()));
    AnnotationMetadata amd = p.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    for (edu.jhu.hlt.concrete.Dependency d : p.getDependencyList())
      b.addDependencies(convert(d));
    return b.build();
  }

  private static final Dependency convert(edu.jhu.hlt.concrete.Dependency d) {
    Dependency.Builder b = new Dependency.Builder();
    b.setDependentIndex(d.getDep());
    if (d.isSetGov()) {
      int gov = d.getGov();
      b.setGovernorIndex(gov);
    }
    b.setNullableEdgeType(d.getEdgeType());
    return b.build();
  }

  private static final Parse convert(edu.jhu.hlt.concrete.Parse p) {
    Parse.Builder b = new Parse.Builder();
    b.setUUID(convert(p.getUuid()));
    AnnotationMetadata amd = p.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    for (edu.jhu.hlt.concrete.Constituent c : p.getConstituentList()) {
      Constituent pc = convert(c);
      b.putConstituents(pc.getId(), pc);
    }

    return b.build();
  }

  private static final Constituent convert(edu.jhu.hlt.concrete.Constituent c) {
    Constituent.Builder b = new Constituent.Builder();
    b.setId(c.getId());
    b.setNullableTag(c.getTag());
    b.addAllChildList(c.getChildList());
    if (c.isSetHeadChildIndex())
      b.setHeadChildIndex(c.getHeadChildIndex());
    if (c.isSetStart())
      b.setStart(c.getStart());
    if (c.isSetEnding())
      b.setEnd(c.getEnding());
    return b.build();
  }

  private static final Token convert (edu.jhu.hlt.concrete.Token tk) {
    Token.Builder b = new Token.Builder();
    b.setIndex(new IntZeroOrGreater.Builder().setVal(tk.getTokenIndex()).build());
    b.setTextSpan(convert(tk.getTextSpan()));
    b.setNullableTokenText(tk.getText());
    return b.build();
  }

  private static final TaggedTokenGroup convert(TokenTagging tt) {
    TaggedTokenGroup.Builder b = new TaggedTokenGroup.Builder();
    b.setUUID(convert(tt.getUuid()));
    AnnotationMetadata amd = tt.getMetadata();
    b.setTool(NonEmptyNonWhitespaceString.create(amd.getTool()))
      .setKBest(IntGreaterThanZero.create(amd.getKBest()))
      .setTimestamp(UnixTimestamp.create(amd.getTimestamp()));
    b.setNullableTaggingType(tt.getTaggingType());
    for (edu.jhu.hlt.concrete.TaggedToken tok : tt.getTaggedTokenList()) {
      TaggedToken pt = convert(tok);
      b.putIndexToTaggedTokenMap(pt.getIndex().getVal(), pt);
    }

    return b.build();
  }

  private static final Map<String, Confidence> convert(List<String> sl, List<Double> dl) {
    Map<String, Confidence> m = new LinkedHashMap<>();
    if (sl == null) {
      LOGGER.warn("String list null: returning empty map.");
      return m;
    } else if (dl == null) {
      LOGGER.warn("Double list null: returning empty map.");
      return m;
    }

    final int tls = sl.size();
    final int cls = dl.size();

    if (tls != 0) {
      if (cls != 0) {
        if (tls == cls) {
          for (int i = 0; i < tls; i++) {
            Confidence c = new Confidence.Builder()
                .setScore(dl.get(i))
                .build();
            m.put(sl.get(i), c);
          }
        }
      } else
        LOGGER.warn("Size of string list [{}] differs from size of double list [{}]. Neither will be added.", tls, cls);
    } else if (cls != 0)
      LOGGER.warn("Double list set but String list is not. Not adding.");

    return m;
  }

  private static final TaggedToken convert(edu.jhu.hlt.concrete.TaggedToken tt) {
    TaggedToken.Builder b = new TaggedToken.Builder();
    b.setIndex(new IntZeroOrGreater.Builder().setVal(tt.getTokenIndex()).build());
    b.setTag(tt.getTag());
    return b.build();
  }
}
