/**
 *
 */
package edu.jhu.hlt.concrete.storers.executables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.services.store.StoreServiceWrapper;
import edu.jhu.hlt.concrete.storers.printer.PrintStorer;
import edu.jhu.hlt.concrete.storers.printer.PrintStorerConfig;

/**
 *
 */
public class PrintStore {

  private static final Logger LOG = LoggerFactory.getLogger(PrintStore.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    // point to the print config
    Config cfg = ConfigFactory.load("printer.conf");
    PrintStorerConfig pcfg = new PrintStorerConfig(cfg);
    // set up the wrapper
    try (PrintStorer storer = new PrintStorer();
        StoreServiceWrapper wrapper = new StoreServiceWrapper(storer.storeImpl(), pcfg.getPort());) {
      LOG.info("preparing to serve on port: {}", pcfg.getPort());
      wrapper.run();
    } catch (Exception e) {
      LOG.info("exception closing storer", e);
    }
  }
}
