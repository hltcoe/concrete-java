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
import java.util.Iterator;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.Serialization;

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
}
