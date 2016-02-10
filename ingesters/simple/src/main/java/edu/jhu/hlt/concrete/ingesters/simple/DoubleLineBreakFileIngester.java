/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.ingesters.simple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.section.TextSpanKindTuple;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Implementation of {@link UTF8FileIngester} whose {@link UTF8FileIngester#fromCharacterBasedFile(Path)}
 * implementation converts the contents of a
 * character-based file to a {@link Communication} object.
 * <ul>
 *  <li>
 *   The file name is used as the ID of the Communication.
 *  </li>
 *  <li>
 *   The Communication will contain one {@link Section} for each double-newline
 *   in the document. For example, on *nix systems, if the contents contain one
 *   instance of '\n\n', the Communication will have two Sections.
 *  </li>
 * </ul>
 */
public class DoubleLineBreakFileIngester implements UTF8FileIngester {

  private static final Logger logger = LoggerFactory.getLogger(DoubleLineBreakFileIngester.class);

  private final String sectionKindLabel;
  private final String lineSep = System.lineSeparator();
  private final String doubleLineSep = lineSep + lineSep;

  private final String commKind;
  private final long ts;

  /**
   * Expect UTF-8 documents.
   */
  public DoubleLineBreakFileIngester(String commKind, String sectionKindLabel) {
    this.commKind = commKind;
    this.sectionKindLabel = sectionKindLabel;
    this.ts = Timing.currentLocalTime();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.FileIngester#fromCharacterBasedFile(java.nio.file.Path, java.nio.charset.Charset)
   */
  @Override
  public Communication fromCharacterBasedFile(Path path) throws IngestException {
    try {
      ExistingNonDirectoryFile f = new ExistingNonDirectoryFile(path);
      try(InputStream is = Files.newInputStream(path);) {
        String content = IOUtils.toString(is, StandardCharsets.UTF_8);
        AnalyticUUIDGeneratorFactory fact = new AnalyticUUIDGeneratorFactory();
        AnalyticUUIDGenerator g = fact.create();
        Communication c = new Communication();
        c.setUuid(g.next());
        c.setId(f.getName());
        c.setText(content);
        c.setType(this.commKind);
        c.setMetadata(TooledMetadataConverter.convert(this));

        String[] split2xNewline = content.split(doubleLineSep);
        Stream.Builder<TextSpanKindTuple> stream = Stream.builder();
        int charCtr = 0;
        for (String s : split2xNewline) {
          final int len = s.length();
          final int sum = len + charCtr;
          TextSpan ts = new TextSpan(charCtr, sum);
          charCtr = sum + 2;
          stream.add(new TextSpanKindTuple(ts, this.sectionKindLabel));
        }

        Stream<Section> sections = new SectionFactory(g).fromTextSpanStream(stream.build());
        sections.forEach(s -> c.addToSectionList(s));
        return c;
      } catch (IOException e) {
        throw new IngestException("Caught exception reading in document.", e);
      }
    } catch (NoSuchFileException | NotFileException e) {
      throw new IngestException("Path did not exist or was a directory.", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.Ingester#getKind()
   */
  @Override
  public String getKind() {
    return this.commKind;
  }

  /**
   * See usage string.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 4) {
      System.err.println("This program converts a character-based file to a .concrete file.");
      System.err.println("The text file must contain UTF-8 encoded characters.");
      System.err.println("If the file contains any double-newlines, the file will be split into sections where those double-newlines occur.");
      System.err.println("The .concrete file will share the same name as the input file, including the extension.");
      System.err.println("This program takes 4 arguments.");
      System.err.println("Argument 1: path/to/a/character/based/file");
      System.err.println("Argument 2: type of Communication to generate [e.g., tweet]");
      System.err.println("Argument 3: type of Sections to generate [e.g., passage]");
      System.err.println("Argument 4: path/to/out/concrete/file");
      System.err.println("Example usage: " + CompleteFileIngester.class.getName()
          + " /my/text/file story passage /my/output/folder");
      System.exit(1);
    }

    String inPathStr = args[0];
    Path inPath = Paths.get(inPathStr);
    try {
      ExistingNonDirectoryFile ef = new ExistingNonDirectoryFile(inPath);
      Optional<String> commType = Optional.ofNullable(args[1]);
      Optional<String> sectionType = Optional.ofNullable(args[2]);
      Optional<String> outPathStr = Optional.ofNullable(args[3]);

      Path ep = ef.getPath();
      String fn = ef.getName();
      Path outPath = Paths.get(outPathStr.get());
      Path outFile = outPath.resolve(fn + ".concrete");

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
        UTF8FileIngester ing = new DoubleLineBreakFileIngester(commType.get(), sectionType.get());
        Communication comm = ing.fromCharacterBasedFile(ep);
        new WritableCommunication(comm).writeToFile(outFile, false);
      } catch (IngestException e) {
        logger.error("Caught exception during ingest.", e);
        System.exit(1);
      } catch (ConcreteException e) {
        logger.error("Caught exception writing output.", e);
      }

    } catch (NoSuchFileException e) {
      logger.error("Path {} does not exist.", inPathStr);
      System.exit(1);
    } catch (NotFileException e) {
      logger.error("Path {} is a directory.", inPathStr);
      System.exit(1);
    }
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return this.ts;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return this.getClass().getName() + " [Project: concrete-ingesters-simple]";
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolNotes()
   */
  @Override
  public List<String> getToolNotes() {
    List<String> sl = new ArrayList<String>();
    sl.add("Communication kind: " + this.commKind);
    sl.add("Section kinds: " + this.sectionKindLabel);
    return sl;
  }
}
