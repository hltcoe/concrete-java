/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;

/**
 * Contains a number of useful utility methods for working with {@link Communication}s.
 */
public class CommunicationUtils {

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

    // ThreadSafeThriftSerializer ser = new ThreadSafeThriftSerializer();
    CommunicationSerializer ser = new CompactCommunicationSerializer();
    for (Path p : pathList)
      loadList.add(ser.fromBytes(Files.readAllBytes(p)));
    return loadList;
  }

  public static List<Communication> loadCommunications(Path pathToFileList) throws ConcreteException, IOException {
    if (!Files.exists(pathToFileList))
      throw new FileNotFoundException("No file at path: " + pathToFileList.toString());
    List<Path> pathList = new ArrayList<>();
    try (Scanner sc = new Scanner(pathToFileList.toFile(), StandardCharsets.UTF_8.toString())) {
      while (sc.hasNextLine())
        pathList.add(Paths.get(sc.nextLine()));
    }

    return loadCommunicationsFromPaths(pathList);
  }

  public static List<Communication> loadCommunications(String pathStringToFileList) throws ConcreteException, IOException {
    return loadCommunications(Paths.get(pathStringToFileList));
  }
}
