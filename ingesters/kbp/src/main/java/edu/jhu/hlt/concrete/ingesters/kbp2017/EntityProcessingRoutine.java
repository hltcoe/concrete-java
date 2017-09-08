package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.routine.Routine;

class EntityProcessingRoutine implements Routine {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntityProcessingRoutine.class);

  private final Map<String, Entity> eMap;
  private final ReceiveChannel<String[]> lines;

  public EntityProcessingRoutine(Map<String, Entity> eMap, ReceiveChannel<String[]> lines) {
    this.eMap = eMap;
    this.lines = lines;
  }

  @Override
  public void run() {
    String previousEntityID = "";
    Entity.Builder bldr = new Entity.Builder();
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
            MentionType mt = MentionType.create(colTwo);
            String txt = line[2];
            txt = txt.substring(1, txt.length() - 1);
            Provenance p = Util.splitSingleColonLine(line[3]);
            Mention m = new Mention.Builder().setText(txt).setType(mt).setProvenance(p).build();
            bldr.addMentions(m);
          } catch (IllegalArgumentException e) {
            LOGGER.warn("can't handle mention type: {}", colTwo);
            continue;
          }
        } else {
          // a true "entity" line
          if (line.length != 5) {
            LOGGER.warn("don't know how to handle entity line: {}", StringUtils.join(line));
            continue;
          }

          Relation.Builder rb = new Relation.Builder();
          rb.setConfidence(Double.parseDouble(line[4]));
          rb.setTarget(line[2]);

          String[] predSplit = colTwo.split(":");
          String eventTxt = predSplit[1];
          int realisPoint = eventTxt.lastIndexOf('.');
          if (realisPoint > 0) {
            // event
            String realisStr = eventTxt.substring(realisPoint+1, eventTxt.length());
            Realis realis = Realis.valueOf(realisStr.toUpperCase(Locale.ENGLISH));
            String event = eventTxt.substring(0, realisPoint);
            rb.setEvent(event);
            rb.addAllProvenances(Util.splitSemicolonLine(line[3]));

            EntityEventPredicate eep = new EntityEventPredicate.Builder()
                .setRelation(rb.build())
                .setRealis(realis)
                .build();
            bldr.addEvents(eep);
          } else {
            // non event
            rb.setEvent(eventTxt);
            // normally have 1 prov statement but can have >1
            String provCol = line[3];
            if (provCol.indexOf(';') > 0) {
              rb.addAllProvenances(Util.splitSemicolonLine(provCol));
            } else {
              // single prov
              Provenance p = Util.splitSingleColonLine(provCol);
              rb.addProvenances(p);
            }

            bldr.addRelations(rb.build());
          }
        }
      }
    } catch (InterruptedException | ChannelClosedException ex) {
      LOGGER.info("Interrupted/Channel closed");
    } finally {
      Entity e = bldr.build();
      if (this.eMap.containsKey(e.getID()))
        throw new IllegalArgumentException("somehow had entity already: " + e.getID());
      LOGGER.debug("Adding last entity: {}", e.getID());
      this.eMap.put(e.getID(), e);
      LOGGER.info("Routine exiting");
    }
  }
}
