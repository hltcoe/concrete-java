package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flipkart.lois.channel.api.ReceiveChannel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.routine.Routine;

class EventProcessingRoutine implements Routine {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessingRoutine.class);

  private final Map<String, Event> eMap;
  private final ReceiveChannel<String[]> lines;
  private final ReceiveChannel<UUID> uuids;

  public EventProcessingRoutine(Map<String, Event> eMap, ReceiveChannel<String[]> lines,
      ReceiveChannel<UUID> uuids) {
    this.eMap = eMap;
    this.lines = lines;
    this.uuids = uuids;
  }

  @Override
  public void run() {
    String previousEntityID = "";
    Event.Builder bldr = new Event.Builder();
    try {
      while (lines.isOpen()) {
        String[] line = lines.receive();
        if (line.length == 0)
          return;
        String id = line[0];

        // if new Event, finish current
        if (!id.equals(previousEntityID)) {
          // skip the first time
          if (!previousEntityID.isEmpty()) {
            Event e = bldr.build();
            if (this.eMap.containsKey(e.getID()))
              throw new IllegalArgumentException("somehow had event already: " + e.getID());
            LOGGER.debug("Adding event: {}", e.getID());
            this.eMap.put(e.getID(), e);
            bldr = new Event.Builder();
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
          bldr.setType(line[2]);
          continue;
        }

        if (colTwo.contains("mention")) {
          // event mention
          final String txt = line[2];
          final String[] byDot = colTwo.split("\\.");
          Provenance prov = Util.splitSingleColonLine(line[3]);
          MentionType mt;
          try {
            mt = MentionType.create(byDot[0]);
          } catch (IllegalArgumentException e) {
            LOGGER.warn("can't handle mention type: {} :: line {}", byDot[0], line);
            continue;
          }
          Mention m = new Mention.Builder()
              .setProvenance(prov)
              .setText(txt.substring(1, txt.length() - 1))
              .setType(mt)
              .setUUID(uuids.receive())
              .build();
          Realis emr;
          try {
            emr = Realis.valueOf(byDot[1].toUpperCase(Locale.ENGLISH));
          } catch (IllegalArgumentException e) {
            LOGGER.warn("can't handle realis type: {} :: line {}", byDot[1], line);
            continue;
          }

          EventMention em = new EventMention.Builder()
              .setMention(m)
              .setRealis(emr)
              .build();
          bldr.addMentions(em);
          continue;
        }

        // a true event
        // split column 2 by colon, then .
        String[] predSplit = colTwo.split(":");
        if (predSplit.length < 2) {
          LOGGER.warn("Got weird line/predicates: {} :: {}", colTwo, line);
          continue;
        }
        String pred = predSplit[0];
        String[] realisSplit = predSplit[1].split("\\.");
        String actor = realisSplit[0];
        String realisStr = realisSplit[1];
        Realis realis = Realis.valueOf(realisStr.toUpperCase(Locale.ENGLISH));

        // spl col 4 by ;
        List<Provenance> provs = Util.splitSemicolonLine(line[3]);
        double conf = Double.parseDouble(line[4]);
        Relation rel = new Relation.Builder()
            .setConfidence(conf)
            .addAllProvenances(provs)
            .setTarget(line[2])
            .setEvent(pred)
            .build();
        EntityEventPredicate eep = new EntityEventPredicate.Builder()
            .setRealis(realis)
            .setRelation(rel)
            .build();
        EventPredicate ep = new EventPredicate.Builder()
            .setAgent(actor)
            .setPredicate(eep)
            .build();
        bldr.addPredicates(ep);
      }
    } catch (InterruptedException | ChannelClosedException ex) {
      // send the current entity
      Event e = bldr.build();
      if (this.eMap.containsKey(e.getID()))
        throw new IllegalArgumentException("somehow had Event already: " + e.getID());
      LOGGER.debug("Adding last Event: {}", e.getID());
      this.eMap.put(e.getID(), e);
    }
  }
}
