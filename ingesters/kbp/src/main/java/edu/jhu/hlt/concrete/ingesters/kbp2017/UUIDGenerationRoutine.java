package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.lois.channel.api.SendChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.routine.Routine;

import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

public class UUIDGenerationRoutine implements Routine {

  private static final Logger LOGGER = LoggerFactory.getLogger(UUIDGenerationRoutine.class);

  private final SendChannel<UUID> chan;
  private final AnalyticUUIDGenerator gen;

  public UUIDGenerationRoutine(SendChannel<UUID> chan, AnalyticUUIDGenerator gen) {
    this.chan = chan;
    this.gen = gen;
  }

  @Override
  public void run() {
    LOGGER.debug("Starting");
    while (this.chan.isOpen()) {
      try {
        this.chan.send(UUID.fromString(this.gen.next().getUuidString()), 5L, TimeUnit.SECONDS);
      } catch (ChannelClosedException | InterruptedException e) {
        LOGGER.info("Interrupted / channel closed");
      } catch (TimeoutException e) {

      }
    }
    LOGGER.info("Routine exiting");
  }
}
