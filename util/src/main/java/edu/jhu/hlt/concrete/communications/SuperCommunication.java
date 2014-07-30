/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import concrete.util.ConcreteEntityized;
import concrete.util.ConcreteSituationized;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Situation;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.SituationSet;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.Serialization;

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
  protected final Serialization ser;
  
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
    this.ser = new Serialization();
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
    return this.comm.isSetSectionSegmentations()
        || this.comm.isSetEntityMentionSets()
        || this.comm.isSetEntitySets()
        || this.comm.isSetSituationMentionSets()
        || this.comm.isSetSituationSets()
        || this.comm.isSetLids();
  }
  
  /**
   * Return a "stripped" {@link SuperCommunication} with extraneous annotations removed.
   */
  public SuperCommunication stripAnnotations() {
    Communication copy = new Communication(this.comm);
    
    // Unset annotation fields if set.
    if (copy.isSetEntitySets())
      copy.unsetEntitySets();
    if (copy.isSetEntityMentionSets())
      copy.unsetEntityMentionSets();
    if (copy.isSetSituationSets())
      copy.unsetSituationSets();
    if (copy.isSetSituationMentionSets())
      copy.unsetSituationMentionSets();
    if (copy.isSetSectionSegmentations())
      copy.unsetSectionSegmentations();
    if (copy.isSetLids())
      copy.unsetLids();

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
   * Return true if this {@link Communication} contains at least 1 {@link SectionSegmentation}
   * that is not empty.
   */
  public boolean containsSectionSegmentation() {
    return this.comm.isSetSectionSegmentations() && this.comm.getSectionSegmentationsSize() > 0;
  }
  
  public boolean containsSection() throws ConcreteException {
    if (!this.containsSectionSegmentation())
      return false;
    SectionSegmentation ss = this.firstSectionSegmentation();
    return ss.isSetSectionList() && ss.getSectionListSize() > 0;
  }
  
  /**
   * Get the first {@link SectionSegmentation} from this {@link Communication}.
   * 
   * If it is not set or is empty, throw a {@link ConcreteException}.
   * 
   * @return the first {@link SectionSegmentation} from the wrapped {@link Communication}
   * @throws ConcreteException
   *           if {@link SectionSegmentation} is not set or is empty
   */
  public SectionSegmentation firstSectionSegmentation() throws ConcreteException {
    if (this.containsSectionSegmentation())
      return this.comm.getSectionSegmentations().get(0);
    else
      throw new ConcreteException("Communication: " + this.comm.getUuid() + " does not have" + " any SectionSegmentations.");
  }

  /**
   * Get the first {@link Section} from the first {@link SectionSegmentation} from the wrapped {@link Communication}.
   * 
   * @return the first {@link Section} from the first {@link SectionSegmentation}
   * @throws ConcreteException
   *           if there is no {@link Section} or {@link SectionSegmentation}
   */
  public Section firstSection() throws ConcreteException {
    SectionSegmentation ss = this.firstSectionSegmentation();
    if (this.hasSections())
      return ss.getSectionList().get(0);
    else
      throw new ConcreteException("SectionSegmentation: " + ss.getUuid() + " does not have" + " any Sections.");
  }

  /**
   * Get the first {@link SentenceSegmentation} from the first {@link Section} from the first {@link SectionSegmentation} of this wrapped {@link Communication}.
   * 
   * @return the first {@link SentenceSegmentation}
   * @throws ConcreteException
   *           if there is no {@link SentenceSegmentation} or any prerequisites are missing
   */
  public SentenceSegmentation firstSentenceSegmentation() throws ConcreteException {
    Section s = this.firstSection();
    if (s.isSetSentenceSegmentation() && s.getSentenceSegmentationSize() > 0)
      return s.getSentenceSegmentation().get(0);
    else
      throw new ConcreteException("Section: " + s.getUuid() + " does not have" + " any Sentence Segmentations.");
  }

  /**
   * Get the first {@link Sentence}.
   * 
   * @return the first {@link Sentence}
   * @throws ConcreteException
   *           if there is no {@link Sentence}, or if any prerequisites are missing.
   */
  public Sentence firstSentence() throws ConcreteException {
    SentenceSegmentation s = this.firstSentenceSegmentation();
    if (s.isSetSentenceList() && s.getSentenceListSize() > 0)
      return s.getSentenceList().get(0);
    else
      throw new ConcreteException("SentenceSegmentation: " + s.getUuid() + " does not have" + " any Sentences.");
  }

  /**
   * Get the first {@link Tokenization}.
   * 
   * @return the first {@link Tokenization}
   * @throws ConcreteException
   *           if there is no {@link Tokenization}, or if any of the prerequisites are missing.
   */
  public Tokenization firstTokenization() throws ConcreteException {
    Sentence s = this.firstSentence();
    if (s.isSetTokenizationList() && s.getTokenizationListSize() > 0)
      return s.getTokenizationList().get(0);
    else
      throw new ConcreteException("Sentence: " + s.getUuid() + " does not have" + " any Tokenizations.");
  }

  /**
   * @return true if {@link SectionSegmentation}(s) are present
   */
  public boolean hasSectionSegmentations() {
    return this.comm.isSetText() && this.comm.isSetSectionSegmentations() && this.comm.getSectionSegmentationsSize() > 0;
  }

  /**
   * @return true if {@link Section}(s) are present in all {@link SectionSegmentation}s
   */
  public boolean hasSections() {
    if (!this.hasSectionSegmentations())
      return false;

    Iterator<SectionSegmentation> i = this.comm.getSectionSegmentationsIterator();
    boolean validSections = true;
    while (validSections && i.hasNext()) {
      SectionSegmentation ss = i.next();
      validSections = ss.isSetSectionList() && ss.getSectionListSize() > 0;
    }

    return validSections;
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
      return new HashMap<UUID, SituationMention>(this.situationMentionIdToSituationMentionMap);
    else {
      Map<UUID, SituationMention> map = new HashMap<UUID, SituationMention>();
      if (this.comm.isSetSituationMentionSets())
        for (SituationMentionSet sms : this.comm.getSituationMentionSets())
          for (SituationMention sm : sms.getMentionList())
            map.put(sm.getUuid(), sm);
      
      this.situationMentionIdToSituationMentionMap = map;
      return new HashMap<>(map);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteTokenized#generateTokenizationIdToTokenizationMap()
   */
  @Override
  public Map<UUID, Tokenization> generateTokenizationIdToTokenizationMap() {
    // if the sentence cache is not set up, do it first.
    if (this.sentIdToSentenceMap == null)
      this.generateSentenceIdToSectionMap();
    
    if (this.tokenizationIdToTokenizationMap != null)
      return new HashMap<>(this.tokenizationIdToTokenizationMap);
    else {
      this.tokenizationIdToTokenizationMap = this.tokenizationIdToTokenizationMap();
      return new HashMap<>(this.tokenizationIdToTokenizationMap);
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
      return new HashMap<>(this.tokenizationIdToTokenIdxToTokenMap);
    else {
      this.tokenizationIdToTokenIdxToTokenMap = this.tokenizationIdToTokenSeqIdToTokensMap();
      return new HashMap<>(this.tokenizationIdToTokenIdxToTokenMap);
    }
  }
  
  private final Map<UUID, Tokenization> tokenizationIdToTokenizationMap() {
    final Map<UUID, Tokenization> toRet = new HashMap<>();
    List<Sentence> stList = new ArrayList<>(this.sentIdToSentenceMap.values());
    for (Sentence st : stList)
      if (st.isSetTokenizationList())
        for (Tokenization t : st.getTokenizationList()) {
          UUID tId = t.getUuid();
          toRet.put(tId, t);
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
    Map<UUID, Map<Integer, Token>> toRet = new HashMap<>();    
    for (Tokenization t : this.tokenizationIdToTokenizationMap.values()) {
      UUID tId = t.getUuid();
      Map<Integer, Token> idToTokenMap = new HashMap<Integer, Token>();
      if (t.isSetTokenList())
        for (Token tok : t.getTokenList().getTokens()) {
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
  public Map<UUID, Sentence> generateSentenceIdToSectionMap() {
    // if the section cache is not set up, do it first.
    if (this.sectionIdToSectionMap == null)
      this.generateSectionIdToSectionMap();
    
    // if we have run this before, just return. 
    if (this.sentIdToSentenceMap != null)
      return new HashMap<>(this.sentIdToSentenceMap);
    else {
      final Map<UUID, Sentence> toRet = new HashMap<>();

      List<Section> sectList = new ArrayList<>(this.sectionIdToSectionMap.values());
      for (Section s : sectList)
        if (s.isSetSentenceSegmentation())
          for (SentenceSegmentation ss : s.getSentenceSegmentation())
            if (ss.isSetSentenceList())
              for (Sentence st : ss.getSentenceList())
                toRet.put(st.getUuid(), st);

      this.sentIdToSentenceMap = toRet;
      return new HashMap<>(toRet);
    }
  }

  /* (non-Javadoc)
   * @see concrete.util.ConcreteSectioned#generateSectionIdToSectionMap()
   */
  @Override
  public Map<UUID, Section> generateSectionIdToSectionMap() {
    // return cached if it exists. otherwise run. 
    if (this.sectionIdToSectionMap != null)
      return new HashMap<>(this.sectionIdToSectionMap);
      
    else {
      final Map<UUID, Section> map = new HashMap<>();

      if (this.comm.isSetSectionSegmentations())
        for (SectionSegmentation ss : this.comm.getSectionSegmentations())
          if (ss.isSetSectionList())
            for (Section s : ss.getSectionList())
              map.put(s.getUuid(), s);

      this.sectionIdToSectionMap = map;
      return new HashMap<>(map);
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
      return new HashMap<>(this.entityMentionIdToEntityMentionMap);
    else {
      Map<UUID, EntityMention> map = new HashMap<UUID, EntityMention>();
      if (this.comm.isSetEntityMentionSets())
        for (EntityMentionSet sms : this.comm.getEntityMentionSets())
          for (EntityMention sm : sms.getMentionSet())
            map.put(sm.getUuid(), sm);
      
      this.entityMentionIdToEntityMentionMap = map;
      return new HashMap<>(map);
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
      return new HashMap<>(this.entityIdToEntityMap);
    else {
      Map<UUID, Entity> map = new HashMap<UUID, Entity>();
      if (this.comm.isSetEntitySets())
        for (EntitySet sms : this.comm.getEntitySets())
          for (Entity sm : sms.getEntityList())
            map.put(sm.getUuid(), sm);
      
      this.entityIdToEntityMap = map;
      return new HashMap<>(map);
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
      return new HashMap<>(this.situationIdToSituationMap);
    else {
      Map<UUID, Situation> map = new HashMap<UUID, Situation>();
      if (this.comm.isSetEntitySets())
        for (SituationSet sms : this.comm.getSituationSets())
          for (Situation sm : sms.getSituationList())
            map.put(sm.getUuid(), sm);
      
      this.situationIdToSituationMap = map;
      return new HashMap<>(map);
    }
  }
}
