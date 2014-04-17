/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;

/**
 * Wrapper around {@link Communication} to allow advanced functionality.
 * 
 * @author max
 */
public class SuperCommunication {

  protected final Communication comm;
  protected final Serialization ser;

  /**
   * 
   */
  public SuperCommunication(Communication comm) {
    this.comm = comm;
    this.ser = new Serialization();
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
   * Get the first {@link SectionSegmentation} from this {@link Communication}.
   * 
   * If it is not set or is empty, throw a {@link ConcreteException}.
   * 
   * @return the first {@link SectionSegmentation} from the wrapped {@link Communication}
   * @throws ConcreteException
   *           if {@link SectionSegmentation} is not set or is empty
   */
  public SectionSegmentation firstSectionSegmentation() throws ConcreteException {
    if (this.comm.isSetSectionSegmentations() && this.comm.getSectionSegmentationsSize() > 0)
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
    if (ss.isSetSectionList() && ss.getSectionListSize() > 0)
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

  /**
   * Iterate over all {@link SectionSegmentation}s and create a {@link Map} of [SectionID, Section].
   * 
   * @return a {@link Map} whose keys contain {@link Section} {@link UUID} strings, and whose values contain {@link Section} objects with that id string.
   */
  public Map<String, Section> sectionIdToSectionMap() {
    final Map<String, Section> toRet = new HashMap<String, Section>();

    if (this.comm.isSetSectionSegmentations())
      for (SectionSegmentation ss : this.comm.getSectionSegmentations())
        if (ss.isSetSectionList())
          for (Section s : ss.getSectionList())
            toRet.put(s.getUuid(), s);

    return toRet;
  }

  /**
   * Return a {@link Map} of [SentenceID, Sentence] for all {@link SentenceSegmentation}s in all {@link SectionSegmentation}s.
   * 
   * @return a {@link Map} whose keys contain {@link Sentence} {@link UUID} strings, and whose values contain {@link Section} objects with that id string.
   */
  public Map<String, Sentence> sentIdToSentenceMap() {
    final Map<String, Sentence> toRet = new HashMap<String, Sentence>();

    List<Section> sectList = new ArrayList<>(this.sectionIdToSectionMap().values());
    for (Section s : sectList)
      if (s.isSetSentenceSegmentation())
        for (SentenceSegmentation ss : s.getSentenceSegmentation())
          if (ss.isSetSentenceList())
            for (Sentence st : ss.getSentenceList())
              toRet.put(st.getUuid(), st);

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
  public Map<String, Map<Integer, Token>> tokenizationIdToTokenSeqIdToTokensMap() {
    final Map<String, Map<Integer, Token>> toRet = new HashMap<>();

    List<Sentence> stList = new ArrayList<>(this.sentIdToSentenceMap().values());
    for (Sentence st : stList)
      if (st.isSetTokenizationList())
        for (Tokenization t : st.getTokenizationList()) {
          String tId = t.getUuid();
          Map<Integer, Token> idToTokenMap = new HashMap<Integer, Token>();
          if (t.isSetTokenList())
            for (Token tok : t.getTokenList())
              idToTokenMap.put(tok.getTokenIndex(), tok);

          toRet.put(tId, idToTokenMap);
        }

    return toRet;
  }
}
