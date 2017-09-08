package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.routine.Routine;

class StringProcessingRoutine implements Routine {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringProcessingRoutine.class);

  private final Map<String, StringEntity> eMap;
  private final ReceiveChannel<String[]> lines;

  public StringProcessingRoutine(Map<String, StringEntity> eMap, ReceiveChannel<String[]> lines) {
    this.eMap = eMap;
    this.lines = lines;
  }

  @Override
  public void run() {
    String previousEntityID = "";
    StringEntity.Builder bldr = new StringEntity.Builder();
    try {
      while (lines.isOpen()) {
        String[] line = lines.receive();
        if (line.length == 0)
          return;
        String id = line[0];

        // if new entity, finish current
        if (!id.equals(previousEntityID)) {
          // skip the first time
          if (!previousEntityID.isEmpty()) {
            StringEntity e = bldr.build();
            if (this.eMap.containsKey(e.getID()))
              throw new IllegalArgumentException("somehow had entity already: " + e.getID());
            LOGGER.debug("Adding entity: {}", e.getID());
            this.eMap.put(e.getID(), e);
            bldr = new StringEntity.Builder();
          }

          bldr.setID(id);
          previousEntityID = id;
        }

        if (line.length < 3) {
          LOGGER.warn("String line is messed up: {}", StringUtils.join(line));
          continue;
        }

        String colTwo = line[1];
        if (colTwo.equals("type"))
          continue;

        MentionType mt = MentionType.create(colTwo);
        Provenance p = Util.splitSingleColonLine(line[3]);
        String txt = line[2].substring(1, line[2].length() - 1);
        Mention m = new Mention.Builder().setText(txt).setType(mt).setProvenance(p).build();

        bldr.addMentions(m);
      }
    } catch (InterruptedException | ChannelClosedException ex) {
      LOGGER.info("Interrupted/Channel closed");
    } finally {
      // send current entity
      StringEntity e = bldr.build();
      if (this.eMap.containsKey(e.getID()))
        throw new IllegalArgumentException("somehow had entity already: " + e.getID());
      LOGGER.debug("Adding last entity: {}", e.getID());
      this.eMap.put(e.getID(), e);
      LOGGER.info("Routine exiting");
    }
  }
}
