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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Constituent;
import edu.jhu.hlt.concrete.Dependency;
import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.communications.SectionedSuperCommunication;
import edu.jhu.hlt.concrete.communications.TokenizedSuperCommunication;

/**
 * @author max
 *
 */
public class CommunicationUtils {

  /**
   * 
   */
  private CommunicationUtils() {

  }
  
  protected static List<Tokenization> getTokenizationList(Collection<Communication> commColl) {
    List<Tokenization> tokList = new ArrayList<>();
    for (Communication c : commColl) {
      TokenizedSuperCommunication ssc = new TokenizedSuperCommunication(c);
      tokList.addAll(ssc.getTokenizationIdToTokenizationMap().values());
    }
    
    return tokList;
  }
  
  public static Set<String> enumerateDependencyParseTags(Collection<Communication> commColl) {
    Set<String> dps = new HashSet<>();
    List<Tokenization> sectList = getTokenizationList(commColl);
    for (Tokenization s : sectList)
      for (DependencyParse dp : s.getDependencyParseList())
        for (Dependency d : dp.getDependencyList())
          dps.add(d.getEdgeType());

    return dps;
  }
  
  public static Set<String> enumeratePartOfSpeechTags(Collection<Communication> commColl) {
    Set<String> dps = new HashSet<>();
    List<Tokenization> sectList = getTokenizationList(commColl);
    for (Tokenization s : sectList)
      for (TaggedToken tt : s.getPosTagList().getTaggedTokenList())
        dps.add(tt.getTag());
    
    return dps;
  }
  
  public static Set<String> enumerateNamedEntityTags(Collection<Communication> commColl) {
    Set<String> dps = new HashSet<>();
    List<Tokenization> sectList = getTokenizationList(commColl);
    for (Tokenization s : sectList)
      for (TaggedToken tt : s.getNerTagList().getTaggedTokenList())
        dps.add(tt.getTag());
    
    return dps;
  }
  
  public static Set<String> enumerateConstituentTags(Collection<Communication> commColl) {
    Set<String> dps = new HashSet<>();
    List<Tokenization> sectList = getTokenizationList(commColl);
    for (Tokenization s : sectList)
      for (Constituent tt : s.getParse().getConstituentList())
        dps.add(tt.getTag());
    
    return dps;
  }
  
  public static Set<String> enumerateSectionKinds(Collection<Communication> commColl) {
    Set<String> ss = new HashSet<>();
    for (Communication c : commColl) {
      SectionedSuperCommunication ssc = new SectionedSuperCommunication(c);
      List<Section> sectList = new ArrayList<>(ssc.getSectionIdToSectionMap().values());
      for (Section s : sectList)
        ss.add(s.getKind());
    }
    
    return ss;
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
}
