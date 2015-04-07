/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.section;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * A class that exposes a method for converting {@link String} objects,
 * representing contents of a {@link Communication} for example, into a single
 * Concrete {@link Section}.
 */
public class SingleSectionSegmenter {
  
  private static final Logger logger = LoggerFactory.getLogger(SingleSectionSegmenter.class);
  
  private SingleSectionSegmenter() {

  }

  /**
   * Create a single {@link Section} based on some text, with the kind set to
   * sectionKind.
   *
   * @param text the {@link String} upon which to create the Section object
   * @param sectionKind the kind of the Section
   * @return a {@link Section} with one {@link TextSpan}.
   */
  public static final Section createSingleSection (String text, String sectionKind) {
    TextSpan ts = new TextSpan(0, text.length());

    Section s = SectionFactory.create();
    s.setKind(sectionKind);
    s.setTextSpan(ts);

    return s;
  }
  
  public static final Section createSingleSection (Communication c, String sectionKind) throws ConcreteException {
    if (!c.isSetText())
      throw new ConcreteException("Text was unset.");
    String ctxt = c.getText();
    if (ctxt.isEmpty())
      throw new ConcreteException("Text was empty.");
    
    TextSpan ts = new TextSpan(0, ctxt.length());

    Section s = SectionFactory.create();
    s.setKind(sectionKind);
    s.setTextSpan(ts);

    return s;
  }
  
  public static void main(String... args) {
    if (args.length != 3) {
      System.err.println("This program converts a Concrete Communication file without sections to a "
          + "Concrete Communication file with one section.");
      System.err.println("The .concrete file will share the same name as the input file, including the extension.");
      System.err.println("This program takes 3 arguments.");
      System.err.println("Argument 1: path/to/a/concrete/communication/file/without/sections");
      System.err.println("Argument 2: type of Section to generate [e.g., passage]");
      System.err.println("Argument 3: path/to/output/folder");
      System.err.println("Example usage: " + SingleSectionSegmenter.class.getName()
          + " /my/comm/file.concrete passage /my/output/folder");
      System.exit(1);
    }
    
    String inPathStr = args[0];
    Path inPath = Paths.get(inPathStr);
    try {
      ExistingNonDirectoryFile ef = new ExistingNonDirectoryFile(inPath);
      Optional<String> sectType = Optional.ofNullable(args[1]);
      Optional<String> outPathStr = Optional.ofNullable(args[2]);

      String fn = ef.getName();
      Path outPath = Paths.get(outPathStr.get());
      Path outFile = outPath.resolve(fn);

      // Output directory exists, or it doesn't.
      // Try to create if it does not.
      if (!Files.exists(outPath)) {
        try {
          Files.createDirectories(outPath);
        } catch (IOException e) {
          logger.error("Caught exception when making output directories.", e);
        }

      // if it does, check to make sure it's a directory.
      } else {
        if (!Files.isDirectory(outPath)) {
          logger.error("Output path exists but is not a directory.");
          System.exit(1);
        } else {
          // check to make sure the output file won't be overwritten.
          if (Files.exists(outFile)) {
            logger.warn("Output file {} exists; not overwriting.", outFile.toString());
            System.exit(1);
          }
        }
      }

      try {
        Communication comm = new CompactCommunicationSerializer().fromPath(inPath);
        // Do not run over sectioned comms. 
        if (comm.isSetSectionList() && comm.getSectionListSize() > 0)
          throw new ConcreteException("Communication has sections previously; not running.");
          
        Section s = createSingleSection(comm, sectType.get());
        comm.addToSectionList(s);
        new SuperCommunication(comm).writeToFile(outFile, false);
      } catch (ConcreteException e) {
        logger.error("Caught exception writing output.", e);
        System.exit(1);
      }

    } catch (NoSuchFileException e) {
      logger.error("Path {} does not exist.", inPathStr);
      System.exit(1);
    } catch (NotFileException e) {
      logger.error("Path {} is a directory.", inPathStr);
      System.exit(1);
    }
  }
}
