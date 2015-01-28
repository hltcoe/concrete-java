/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import concrete.tools.AnnotationException;
import concrete.util.ConcreteEntityized;
import concrete.util.ConcreteSituationized;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Situation;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.SituationSet;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ConcreteUUIDFactory;

/**
 * <strong>Read-only</strong> wrapper around {@link Communication} to allow advanced functionality.
 * <br>
 * <br>
 * Be aware that changes to wrapped {@link Communication} objects are not propagated through to the
 * {@link SuperCommunication} object.
 *
 * @author max
 */
public class SuperCommunication implements ConcreteSituationized, ConcreteEntityized {

  protected final Communication comm;
  protected final CommunicationSerializer ser;
  protected final ConcreteUUIDFactory idf = new ConcreteUUIDFactory();

  protected Map<UUID, Section> sectionIdToSectionMap;
  protected Map<UUID, Sentence> sentIdToSentenceMap;
  protected Map<UUID, Tokenization> tokenizationIdToTokenizationMap;
  protected Map<UUID, Map<Integer, Token>> tokenizationIdToTokenIdxToTokenMap;

  protected Map<UUID, SituationMention> situationMentionIdToSituationMentionMap;
  protected Map<UUID, Situation> situationIdToSituationMap;

  protected Map<UUID, EntityMention> entityMentionIdToEntityMentionMap;
  protected Map<UUID, Entity> entityIdToEntityMap;

  /**
   * Single arg ctor: pass in a {@link Communication} object to wrap.
   */
  public SuperCommunication(Communication comm) {
    // Create a copy, providing some immutability.
    this.comm = new Communication(comm);
    this.ser = new CompactCommunicationSerializer();
  }

  /**
   * Return a {@link Section} that encompasses the entire {@link Communication} .text
   * field.
   *
   * @param sectionKind the kind of section to generate
   * @return a {@link Section} with the appropriate kind that spans the entire text
   * @throws AnnotationException if the .text field is not set in this {@link Communication} object.
   */
  public Section singleSection(String sectionKind) throws AnnotationException {
    if (!this.comm.isSetText())
      throw new AnnotationException("This method requires the .text field to be set.");

    Section s = new Section(this.idf.getConcreteUUID(), sectionKind);
    TextSpan ts = new TextSpan(0, this.comm.getText().length());
    s.setTextSpan(ts);

    return s;
  }

  /**
   * Returns a <b>copy</b> of the {@link Communication} wrapped by this {@link SuperCommunication}.
   *
   * If you modify the copy, modifications will not show up.
   */
  public Communication getCopy() {
    return new Communication(this.comm);
  }

  public String getId() {
    return this.comm.getId();
  }

  public UUID getUuid() {
    return this.comm.getUuid();
  }

  /**
   * True if this {@link Communication} contains annotations that are not part of a
   * "root" {@link Communication}. Used in rebar to see if any dangling annotations
   * exist and chop them off if needed.
   */
  public boolean containsAnnotations() {
    throw new UnsupportedOperationException("This method is not currently supported.");
//    return this.comm.isSetEntityMentionSetList()
//        || this.comm.isSetEntitySetList()
//        || this.comm.isSetSituationMentionSetList()
//        || this.comm.isSetSituationSetList()
//        || this.comm.isSetLidList();
  }

  /**
   * Return a "stripped" {@link SuperCommunication} with extraneous annotations removed.
   */
  public SuperCommunication stripAnnotations() {
    Communication copy = new Communication(this.comm);

    // Unset annotation fields if set.
    if (copy.isSetEntitySetList())
      copy.unsetEntitySetList();
    if (copy.isSetEntityMentionSetList())
      copy.unsetEntityMentionSetList();
    if (copy.isSetSituationSetList())
      copy.unsetSituationSetList();
    if (copy.isSetSituationMentionSetList())
      copy.unsetSituationMentionSetList();
    if (copy.isSetLidList())
      copy.unsetLidList();

    return new SuperCommunication(copy);
  }

  /**
   * Take in a {@link Path} to an output file, and whether or not to delete the file at that path if it already exists, and output a byte array that represents
   * a serialized {@link Communication} object.
   *
   * @param path
   *          - a {@link Path} to the destination of the serialized {@link Communication}.
   * @param deleteExisting
   *          - whether to delete the file at path, if it exists.
   * @throws ConcreteException
   *           if there are {@link IOException}s or {@link TException}s.
   */
  public void writeToFile(Path path, boolean deleteExisting) throws ConcreteException {
    try {
      if (deleteExisting)
        Files.deleteIfExists(path);
      else if (Files.exists(path))
        throw new ConcreteException("File exists at: " + path.toString() + ". Delete it, or " + "call this method with the second parameter set to 'true'.");

      byte[] bytez = this.ser.toBytes(this.comm);
      Files.write(path, bytez);
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }

  /**
   * Wrapper around {@link #writeToFile(Path, boolean)} that takes a {@link String} instead of a {@link Path}.
   *
   * @see #writeToFile(Path, boolean)
   *
   * @param pathString
   * @param deleteExisting
   *          - whether to delete the file at path, if it exists.
   * @throws ConcreteException
   *           if there are {@link IOException}s or {@link TException}s.
   */
  public void writeToFile(String pathString, boolean deleteExisting) throws ConcreteException {
    this.writeToFile(Paths.get(pathString), deleteExisting);
  }

  /**
   * Get the first {@link Section} from the wrapped {@link Communication}.
   *
   * @return the first {@link Section}
   * @throws ConcreteException
   *           if there is no {@link Section}
   */
  public Section firstSection() throws ConcreteException {
    if (this.hasSections())
      return this.comm.getSectionList().get(0);
    else
      throw new ConcreteException("No such section exists.");
  }

  /**
   * Get the first {@link Sentence}.
   *
   * @return the first {@link Sentence}
   * @throws ConcreteException
   *           if there is no {@link Sentence}, or if any prerequisites are missing.
   */
  public Sentence firstSentence() throws ConcreteException {
    if (this.hasSections()) {
      Section s = this.firstSection();
      if (s.isSetSentenceList() && s.getSentenceListSize() > 0)
        return s.getSentenceListIterator().next();
      else
        throw new ConcreteException("This communication does not have sentences.");
    } else {
      throw new ConcreteException("No sections, so no sentences in this Communication.");
    }
  }

  /**
   * Get the first {@link Tokenization}.
   *
   * @return the first {@link Tokenization}
   * @throws ConcreteException
   *           if there is no {@link Tokenization}, or if any of the prerequisites are missing.
   */
  public Tokenization firstTokenization() throws ConcreteException {
    if (this.hasSections()) {
      Section s = this.firstSection();
      if (s.isSetSentenceList() && s.getSentenceListSize() > 0) {
        Sentence firstSent =  s.getSentenceListIterator().next();
        if (firstSent.isSetTokenization())
          return firstSent.getTokenization();
        else
          throw new ConcreteException("The first sentence does not have a tokenization.");
      } else
        throw new ConcreteException("This communication does not have sentences in the first section.");
    } else {
      throw new ConcreteException("No sections, so no tokenizations in this Communication.");
    }
  }

  /**
   * @return true if {@link Section}(s) are present
   */
  public boolean hasSections() {
    return this.comm.isSetSectionList() && this.comm.getSectionListSize() > 0;
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteSituationMentionized#generateSituationMentionIdToSituationMentionMap()
   */
  @Override
  public Map<UUID, SituationMention> generateSituationMentionIdToSituationMentionMap() {
    // if the tokenization cache is not set up, do it first.
    // TODO: needed?
//    if (this.tokenizationIdToTokenizationMap == null)
//      this.generateTokenizationIdToTokenizationMap();

    if (this.situationMentionIdToSituationMentionMap != null)
      return new LinkedHashMap<UUID, SituationMention>(this.situationMentionIdToSituationMentionMap);
    else {
      Map<UUID, SituationMention> map = new LinkedHashMap<UUID, SituationMention>();
      if (this.comm.isSetSituationMentionSetList())
        for (SituationMentionSet sms : this.comm.getSituationMentionSetList())
          for (SituationMention sm : sms.getMentionList())
            map.put(sm.getUuid(), sm);

      this.situationMentionIdToSituationMentionMap = map;
      return new LinkedHashMap<>(map);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteTokenized#generateTokenizationIdToTokenizationMap()
   */
  @Override
  public Map<UUID, Tokenization> generateTokenizationIdToTokenizationMap() {
    // if the sentence cache is not set up, do it first.
    if (this.sentIdToSentenceMap == null)
      this.generateSentenceIdToSentenceMap();

    if (this.tokenizationIdToTokenizationMap != null)
      return new LinkedHashMap<>(this.tokenizationIdToTokenizationMap);
    else {
      this.tokenizationIdToTokenizationMap = this.tokenizationIdToTokenizationMap();
      return new LinkedHashMap<>(this.tokenizationIdToTokenizationMap);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteTokenized#generateTokenizationIdToTokenIdxToTokenMap()
   */
  @Override
  public Map<UUID, Map<Integer, Token>> generateTokenizationIdToTokenIdxToTokenMap() {
    // if the tokenization cache is not set up, do it first.
    if (this.tokenizationIdToTokenizationMap == null)
      this.generateTokenizationIdToTokenizationMap();

    if (this.tokenizationIdToTokenIdxToTokenMap != null)
      return new LinkedHashMap<>(this.tokenizationIdToTokenIdxToTokenMap);
    else {
      this.tokenizationIdToTokenIdxToTokenMap = this.tokenizationIdToTokenSeqIdToTokensMap();
      return new LinkedHashMap<>(this.tokenizationIdToTokenIdxToTokenMap);
    }
  }

  private final Map<UUID, Tokenization> tokenizationIdToTokenizationMap() {
    final Map<UUID, Tokenization> toRet = new LinkedHashMap<>();
    List<Sentence> stList = new ArrayList<>(this.sentIdToSentenceMap.values());
    for (Sentence st : stList) {
      Tokenization tok = st.getTokenization();
      UUID tId = tok.getUuid();
      toRet.put(tId, tok);
    }

    return toRet;
  }

  /**
   * Returns a nested map. <br>
   * <br>
   * Top level: <br>
   * Key: Tokenization ID <br>
   * Value: Map[Integer, Token] that represents [ID, Token] for this {@link Tokenization} <br>
   * <br>
   * Nested Map: <br>
   * Key: Token Sequence ID <br>
   * Value: {@link Token} object
   *
   */
  private Map<UUID, Map<Integer, Token>> tokenizationIdToTokenSeqIdToTokensMap() {
    Map<UUID, Map<Integer, Token>> toRet = new LinkedHashMap<>();
    for (Tokenization t : this.tokenizationIdToTokenizationMap.values()) {
      UUID tId = t.getUuid();
      Map<Integer, Token> idToTokenMap = new LinkedHashMap<Integer, Token>();
      if (t.isSetTokenList())
        for (Token tok : t.getTokenList().getTokenList()) {
          idToTokenMap.put(tok.getTokenIndex(), tok);
          toRet.put(tId, idToTokenMap);
        }
    }

    return toRet;
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteSentenced#generateSentenceIdToSectionMap()
   */
  @Override
  public Map<UUID, Sentence> generateSentenceIdToSentenceMap() {
    // if the section cache is not set up, do it first.
    if (this.sectionIdToSectionMap == null)
      this.generateSectionIdToSectionMap();

    // if run before, return.
    if (this.sentIdToSentenceMap != null)
      return new LinkedHashMap<>(this.sentIdToSentenceMap);
    else {
      final Map<UUID, Sentence> toRet = new LinkedHashMap<>();

      List<Section> sectList = new ArrayList<>(this.sectionIdToSectionMap.values());
      for (Section s : sectList)
        if (s.isSetSentenceList())
          for (Sentence st : s.getSentenceList())
            toRet.put(st.getUuid(), st);

      this.sentIdToSentenceMap = toRet;
      return new LinkedHashMap<>(toRet);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteSectioned#generateSectionIdToSectionMap()
   */
  @Override
  public Map<UUID, Section> generateSectionIdToSectionMap() {
    // return cached if it exists. otherwise run.
    if (this.sectionIdToSectionMap != null)
      return new LinkedHashMap<>(this.sectionIdToSectionMap);

    else {
      final Map<UUID, Section> map = new LinkedHashMap<>();
      for (Section s : this.comm.getSectionList())
        map.put(s.getUuid(), s);

      this.sectionIdToSectionMap = map;
      return new LinkedHashMap<>(map);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteEntityMentionized#generateEntityMentionIdToEntityMentionMap()
   */
  @Override
  public Map<UUID, EntityMention> generateEntityMentionIdToEntityMentionMap() {
    // if the tokenization cache is not set up, do it first.
    // TODO: needed?
//    if (this.tokenizationIdToTokenizationMap == null)
//      this.generateTokenizationIdToTokenizationMap();

    if (this.entityMentionIdToEntityMentionMap != null)
      return new LinkedHashMap<>(this.entityMentionIdToEntityMentionMap);
    else {
      Map<UUID, EntityMention> map = new LinkedHashMap<UUID, EntityMention>();
      if (this.comm.isSetEntityMentionSetList())
        for (EntityMentionSet sms : this.comm.getEntityMentionSetList())
          for (EntityMention sm : sms.getMentionList())
            map.put(sm.getUuid(), sm);

      this.entityMentionIdToEntityMentionMap = map;
      return new LinkedHashMap<>(map);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteEntityized#generateEntityIdToEntityMap()
   */
  @Override
  public Map<UUID, Entity> generateEntityIdToEntityMap() {
    // if the tokenization cache is not set up, do it first.
    if (this.entityMentionIdToEntityMentionMap == null)
      this.generateEntityMentionIdToEntityMentionMap();

    if (this.entityIdToEntityMap != null)
      return new LinkedHashMap<>(this.entityIdToEntityMap);
    else {
      Map<UUID, Entity> map = new LinkedHashMap<UUID, Entity>();
      if (this.comm.isSetEntitySetList())
        for (EntitySet sms : this.comm.getEntitySetList())
          for (Entity sm : sms.getEntityList())
            map.put(sm.getUuid(), sm);

      this.entityIdToEntityMap = map;
      return new LinkedHashMap<>(map);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteSituationized#generateSituationIdToSituationMap()
   */
  @Override
  public Map<UUID, Situation> generateSituationIdToSituationMap() {
    // if the tokenization cache is not set up, do it first.
    if (this.situationMentionIdToSituationMentionMap == null)
      this.generateSituationMentionIdToSituationMentionMap();

    if (this.situationIdToSituationMap != null)
      return new LinkedHashMap<>(this.situationIdToSituationMap);
    else {
      Map<UUID, Situation> map = new LinkedHashMap<UUID, Situation>();
      if (this.comm.isSetEntitySetList())
        for (SituationSet sms : this.comm.getSituationSetList())
          for (Situation sm : sms.getSituationList())
            map.put(sm.getUuid(), sm);

      this.situationIdToSituationMap = map;
      return new LinkedHashMap<>(map);
    }
  }
}
