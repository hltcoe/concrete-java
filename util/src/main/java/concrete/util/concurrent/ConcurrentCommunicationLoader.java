/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package concrete.util.concurrent;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.jhu.hlt.concrete.Communication;

/**
 * This class can be used to load {@link Communication} objects from disk in parallel.  
 * 
 * @author max
 */
public class ConcurrentCommunicationLoader implements AutoCloseable {

  private final ExecutorService runner;
  private final CompletionService<Communication> bytesToCommService;
  
  /**
   * Single arg ctor: pass in the desired number of threads. 
   */
  public ConcurrentCommunicationLoader(int nThreads) {
    this.runner = Executors.newFixedThreadPool(nThreads);
    this.bytesToCommService = new ExecutorCompletionService<Communication>(this.runner);
  }
  
  /**
   * No arg ctor: use {@link Runtime} to determine number of threads to use via call to availableProcessors().
   */
  public ConcurrentCommunicationLoader() {
    this(Runtime.getRuntime().availableProcessors());
  }
  
  /**
   * Deserialize {@link Communication} objects in parallel. 
   * <br>
   * <br>
   * The {@link ExecutorCompletionService} guarantees that the objects are returned in the order that they are queued. 
   * In other words, one can safely iterate over the returned object and wait without truly blocking. 
   * 
   * @param pathToCommFiles - path to a text file containing paths on disk to serialized {@link Communication} files.
   * @return a {@link List} of {@link Future} objects with a {@link Communication} expected. 
   * @throws FileNotFoundException if the passed in {@link Path} does not exist on disk. 
   */
  public List<Future<Communication>> bulkLoad(Path pathToCommFiles) throws FileNotFoundException {
    List<Path> paths = new ArrayList<>();
    try(Scanner sc = new Scanner(pathToCommFiles.toFile())) {
      while (sc.hasNextLine())
        paths.add(Paths.get(sc.nextLine()));
    }
    
    CompletionService<Communication> srv = new ExecutorCompletionService<>(this.runner);
    List<Future<Communication>> commList = new ArrayList<>();
    for (Path p : paths) {
      Future<Communication> f = srv.submit(new CallablePathToCommunication(p));
      commList.add(f);
    }

    return commList;
  }
  
  public List<Future<Communication>> bulkLoad(String pathToCommFilesString) throws FileNotFoundException {
    return this.bulkLoad(Paths.get(pathToCommFilesString));
  }
  
  public Future<Communication> fromBytes(byte[] bytes) {
    return this.bytesToCommService.submit(new CallableBytesToCommunication(bytes));
  }

  /**
   * When called, shutdown the {@link ExecutorService} object and block until it is finished running.
   */
  /* (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    this.runner.shutdown();
    this.runner.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
  }
}
