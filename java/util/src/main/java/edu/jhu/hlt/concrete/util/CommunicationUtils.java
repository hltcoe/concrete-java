/*
 *
 */
package edu.jhu.hlt.concrete.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import concrete.util.concurrent.ConcurrentCommunicationLoader;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.communications.SectionedSuperCommunication;
import edu.jhu.hlt.concrete.communications.TokenizedSuperCommunication;

/**
 * Contains a number of useful utility methods for working with {@link Communication}s.
 * 
 * @author max
 */
public class CommunicationUtils {

  private static final Logger logger = LoggerFactory.getLogger(CommunicationUtils.class);

  /**
   *
   */
  private CommunicationUtils() {

  }

  public static List<Communication> loadCommunicationsFromPathStrings(List<String> pathStringList) throws ConcreteException, IOException {
    List<Path> pathList = new ArrayList<>();
    for (String s : pathStringList) {
      Path p = Paths.get(s);
      if (!Files.exists(p))
        throw new FileNotFoundException("No file found at path: " + p.toString());
    }

    return CommunicationUtils.loadCommunicationsFromPaths(pathList);
  }

  public static List<Communication> loadCommunicationsFromPaths(List<Path> pathList) throws ConcreteException, IOException {
    List<Communication> loadList = new ArrayList<>(pathList.size());

    Serialization ser = new Serialization();
    for (Path p : pathList)
      loadList.add(ser.fromBytes(new Communication(), Files.readAllBytes(p)));
    return loadList;
  }

  public static List<Communication> loadCommunications(Path pathToFileList) throws ConcreteException, IOException {
    if (!Files.exists(pathToFileList))
      throw new FileNotFoundException("No file at path: " + pathToFileList.toString());
    List<Path> pathList = new ArrayList<>();
    try (Scanner sc = new Scanner(pathToFileList.toFile())) {
      while (sc.hasNextLine())
        pathList.add(Paths.get(sc.nextLine()));
    }

    return loadCommunicationsFromPaths(pathList);
  }

  public static List<Communication> loadCommunications(String pathStringToFileList) throws ConcreteException, IOException {
    return loadCommunications(Paths.get(pathStringToFileList));
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      logger.info("Usage: {} <path-to-file-list> <type>", CommunicationUtils.class.getSimpleName());
      logger.info("Available types (case insensitive):");
      logger.info("SectionKinds");
      logger.info("POSTags");
      logger.info("NERTags");
      logger.info("DependencyParseTags");
      logger.info("ConstituentTags");
      logger.info("e.g., {} {} {}", CommunicationUtils.class.getSimpleName(), "my/file/list.txt", "SectionKinds");
      System.exit(1);
    }

    String fileListString = args[0];
    String toDump = args[1];

    List<Future<Communication>> commList;
    try (ConcurrentCommunicationLoader ccl = new ConcurrentCommunicationLoader(Runtime.getRuntime().availableProcessors())) {
      commList = ccl.bulkLoad(fileListString);
    }
    
    Set<String> strings = new HashSet<>();
    switch (toDump.toLowerCase()) {
    case "sectionkinds":
      for (Future<Communication> comm : commList) 
        strings.addAll(new SectionedSuperCommunication(comm.get()).enumerateSectionKinds());
      break;
    case "postags":
      for (Future<Communication> comm : commList) 
        strings.addAll(new TokenizedSuperCommunication(comm.get()).enumeratePartOfSpeechTags());      
      break;
    case "nertags":
      for (Future<Communication> comm : commList) 
        strings.addAll(new TokenizedSuperCommunication(comm.get()).enumerateNamedEntityTags());      
      break;
    case "dependencyparsetags":
      for (Future<Communication> comm : commList) 
        strings.addAll(new TokenizedSuperCommunication(comm.get()).enumerateDependencyParseTags());      
      break;
    case "constituenttags":
      for (Future<Communication> comm : commList) 
        strings.addAll(new TokenizedSuperCommunication(comm.get()).enumerateConstituentTags());
      break;
      default:
        logger.error("You can't dump that type. Run this without arguments for a list of types.");
        System.exit(1);
    }
    
    for (String s : strings)
      System.out.println(s);
  }
}
