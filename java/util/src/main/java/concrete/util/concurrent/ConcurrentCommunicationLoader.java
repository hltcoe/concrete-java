/*
 * 
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
 * @author max
 *
 */
public class ConcurrentCommunicationLoader implements AutoCloseable {

  private final ExecutorService runner;
  
  /**
   * 
   */
  public ConcurrentCommunicationLoader(int nThreads) {
    this.runner = Executors.newFixedThreadPool(nThreads);
  }
  
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

  /* (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    this.runner.shutdown();
    this.runner.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
  }
}
