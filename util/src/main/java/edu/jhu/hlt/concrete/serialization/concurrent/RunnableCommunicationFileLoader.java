/**
 *
 */
package edu.jhu.hlt.concrete.serialization.concurrent;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.iterators.TarGzArchiveEntryCommunicationIterator;
import edu.jhu.hlt.utilt.AutoCloseableIterator;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Implementation of {@link Runnable} that allows a thread pool
 * to load {@link Communication} objects from <code>.tar.gz</code>
 * files.
 */
public class RunnableCommunicationFileLoader implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(RunnableCommunicationFileLoader.class);

  private final ExistingNonDirectoryFile p;
  private final BlockingQueue<Communication> q;

  /**
   * @param p
   *          the {@link Path} from which to read files. Must be a
   *          <code>.tar.gz</code> file of {@link Communication} objects.
   * @param q
   *          the {@link ArrayBlockingQueue} to put communications on
   * @throws NotFileException
   *           if the path is a directory
   * @throws NoSuchFileException
   *           if nothing exists at the path object
   */
  public RunnableCommunicationFileLoader(Path p, BlockingQueue<Communication> q)
      throws NoSuchFileException, NotFileException {
    this(new ExistingNonDirectoryFile(p), q);
  }

  /**
   * @param f an {@link ExistingNonDirectoryFile} <code>.tar.gz</code> of {@link Communication} objects
   * @param q the {@link ArrayBlockingQueue} to push communications on to
   */
  public RunnableCommunicationFileLoader(ExistingNonDirectoryFile f, BlockingQueue<Communication> q) {
    this.p = f;
    this.q = q;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try (AutoCloseableIterator<Communication> iter =
        TarGzArchiveEntryCommunicationIterator.fromPath(this.p.getPath())) {
      while (iter.hasNext())
        this.q.put(iter.next());
    } catch (IOException e) {
      LOGGER.error("Caught exception while interacting with the stream: {}", e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Caught exception whilst closing the stream: {}", e.getMessage());
    }
  }
}
