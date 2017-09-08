package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.routine.Routine;

class EventProcessingRoutine implements Routine {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessingRoutine.class);

  private final Map<String, Entity> eMap;
  private final ReceiveChannel<String[]> lines;

  public EventProcessingRoutine(Map<String, Entity> eMap, ReceiveChannel<String[]> lines) {
    this.eMap = eMap;
    this.lines = lines;
  }

  @Override
  public void run() {
    String previousEntityID = "";
    Entity.Builder bldr = new Entity.Builder();
    try {
      while (true) {
        String[] line = lines.receive();
        String id = line[0];

        // if new entity, finish current
        if (!id.equals(previousEntityID)) {
          // skip the first time
          if (!previousEntityID.isEmpty()) {
            Entity e = bldr.build();
            if (this.eMap.containsKey(e.getID()))
              throw new IllegalArgumentException("somehow had entity already: " + e.getID());
            LOGGER.debug("Adding entity: {}", e.getID());
            this.eMap.put(e.getID(), e);
            bldr = new Entity.Builder();
          }

          bldr.setID(id);
          previousEntityID = id;
        }

        if (line.length < 3) {
          LOGGER.warn("Event line is messed up: {}", StringUtils.join(line));
          continue;
        }

        String colTwo = line[1];
        if (colTwo.equals("link"))
          continue;
        if (colTwo.equals("type")) {
          bldr.setType(EntityType.valueOf(line[2]));
          continue;
        }

        if (colTwo.endsWith("mention")) {
          try {
            MentionType mt = MentionType.valueOf(colTwo);
            String txt = line[2];
            txt = txt.substring(1, txt.length() - 1);
            Mention m = new Mention.Builder().setText(txt).setType(mt).build();
            bldr.addMentions(m);
          } catch (IllegalArgumentException e) {
            LOGGER.warn("can't handle mention type: {}", colTwo);
            continue;
          }
        }
      }
    } catch (InterruptedException | ChannelClosedException ex) {
      // send the current entity
      Entity e = bldr.build();
      if (this.eMap.containsKey(e.getID()))
        throw new IllegalArgumentException("somehow had entity already: " + e.getID());
      LOGGER.debug("Adding last entity: {}", e.getID());
      this.eMap.put(e.getID(), e);
    }
  }
}
