package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Argument;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.MentionArgument;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Situation;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.SituationSet;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Entity;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Event;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Mention;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Provenance;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Relation;
import edu.jhu.hlt.concrete.ingesters.kbp2017.SubmittedKB;
import edu.jhu.hlt.concrete.ingesters.kbp2017.TextSpan;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadataFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

public class ConcreteSubmittedKB {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteSubmittedKB.class);
  private static final ObjectMapper om = new ObjectMapper();

  private final Map<String, Entity> idToEntityMap;
  private final Map<String, Event> idToEventMap;

  private final Multimap<String, Entity> documentIDToEntityMap;
  // private final Multimap<String, Event> documentIDToEventMap;

  private final AnnotationMetadata amd;

  public static SubmittedKB readSubmittedKB(InputStream in) throws JsonParseException, JsonMappingException, IOException {
    try (InputStreamReader irdr = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader rdr = new BufferedReader(irdr);) {
      return om.readValue(rdr, SubmittedKB.class);
    }
  }

  public ConcreteSubmittedKB(InputStream in) throws IOException {
    SubmittedKB skb = readSubmittedKB(in);
    LOGGER.info("KB deserialized OK");

    this.idToEntityMap = skb.getEntityMap();
    this.idToEventMap = skb.getEventMap();
    this.amd = AnnotationMetadataFactory.fromCurrentLocalTime("tac-kbp-2017-" + skb.getKBName());
    LOGGER.info("Creating document-based entity mapping");
    this.documentIDToEntityMap = this.documentBasedEntities();
    // this.documentIDToEventMap = this.documentBasedEvents();
  }

  public Map<String, Entity> idToEntityMap() {
    return this.idToEntityMap;
  }

  public Multimap<String, Entity> documentBasedEntities() {
    LOGGER.info("Creating document-based view");
    Multimap<String, Entity> mm = HashMultimap.create();
    ExecutorService ste = Executors.newSingleThreadExecutor();

    ArrayBlockingQueue<Entity> buffer = new ArrayBlockingQueue<>(100);
    Runnable load = () -> {
      while (true) {
        try {
          Entity le = buffer.poll(3L, TimeUnit.SECONDS);
          if (le != null) {
            for (String docID : le.getDocumentIDs()) {
              mm.put(docID, le);
            }
          } else
            return;
        } catch (InterruptedException e) {

        }
      }
    };

    Future<?> f = ste.submit(load);
    this.idToEntityMap.values().parallelStream().forEach(e -> {
      try {
        buffer.put(e);
      } catch (InterruptedException e1) {

      }
    });
    try {
      f.get();
      ste.shutdown();
      ste.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error("failed to set up right");
      throw new RuntimeException();
    }

    LOGGER.info("Document-based view created: {} items", mm.size());
    return mm;
  }

  public Multimap<String, Event> documentBasedEvents() {
    Multimap<String, Event> mm = HashMultimap.create();
    for (Event e : this.idToEventMap.values()) {
      for (String docID : e.getDocumentIDs()) {
        mm.put(docID, e);
      }
    }
    return mm;
  }

  public static List<LocalTokenization> getTokenizations(Communication c) {
    ImmutableList.Builder<LocalTokenization> b = ImmutableList.builder();
    if (c.isSetSectionList())
      for (Section s : c.getSectionList())
        if (s.isSetSentenceList())
          for (int i = 0; i < s.getSentenceListSize(); i++) {
            Sentence st = s.getSentenceList().get(i);
            if (st.isSetTokenization())
              b.add(new LocalTokenization(st.getTokenization()));
          }

    return b.build();
  }

  public Communication process(Communication c) {
    Communication cpy = new Communication(c);
    final String id = cpy.getId();

    if (this.documentIDToEntityMap.containsKey(id)) {
      List<LocalTokenization> ltkzl = getTokenizations(cpy);

      AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
      AnalyticUUIDGenerator gen = f.create();

      EntityMentionSet ems = this.createEMS(gen);
      EntitySet es = this.createES(gen);
      SituationMentionSet sms = this.createSMS(gen);
      SituationSet ss = this.createSS(gen);

      for (Entity e : this.documentIDToEntityMap.get(id)) {
        Map<Provenance, UUID> provToIDMap = new HashMap<>();

        // phase 1 - create concrete entity
        edu.jhu.hlt.concrete.Entity concE = e.toConcrete();
        // phase 2 - create entity mentions from mentions
        // only want those inside this document
        List<Mention> withinDocMentions = e.getMentions().stream()
          .filter(m -> m.withinDocument(id))
          .collect(Collectors.toList());
        // also need to persist a data structure to lookup entity mentions
        // from Provenance objects in order to align MentionArguments
        for (Mention m : withinDocMentions) {
          EntityMention em = new EntityMention()
              .setUuid(gen.next())
              .setText(m.getText())
              .setEntityType(m.getType().toString());
          Provenance p = m.getProvenance();
          TextSpan lts = p.getConcreteStyleTextSpan();
          if (provToIDMap.containsKey(p))
            LOGGER.debug("[document={}] Duplicate provenance: {}", id, p.toString());
          else
            provToIDMap.put(p, em.getUuid());
          // TODO plug token alignment
          for (LocalTokenization ltkz : ltkzl) {
            if (ltkz.getTextSpan().overlaps(lts)) {
              ltkz.generateTRS(lts).ifPresent(em::setTokens);
              break;
            }
          }

          if (!em.isSetTokens()) {
            LOGGER.info("[doc={}] Failed to find supporting tokens for mention: {}", id, m.toString());
            continue;
          }

          ems.addToMentionList(em);
          concE.addToMentionIdList(em.getUuid());
        }

        if (!concE.isSetMentionIdList()) {
          LOGGER.warn("[doc={}] Failed to find any mentions for entity: {} even though it has {} mentions",
              id, e.getID(), withinDocMentions.size());
          continue;
        } else {
          es.addToEntityList(concE);
          es.setMentionSetId(ems.getUuid());
        }

        // Map<Provenance, UUID> pToEMIDMap = ImmutableMap.copyOf(provToIDMap);
        // next - relations
        // max thinks each relation is a situation,
        // each provenance representing a sit mention

        // only target relations that are refed w/ in this document
        List<Relation> withinDocRels = e.getRelations().stream()
            .filter(r -> r.containsAtLeastOneProvenanceFromDocument(id))
            .collect(Collectors.toList());
        for (Relation r : withinDocRels) {
          // create situation
          Situation sit = new Situation().setUuid(gen.next());
          // TODO fix w/in ingester
          sit.setSituationType("UNK");
          sit.setSituationKind(r.getEvent());
          sit.setConfidence(r.getConfidence());

          if (this.idToEntityMap.containsKey(r.getTarget())) {
            Argument a = new Argument();
            a.setEntityId(UUIDFactory.fromJavaUUID(this.idToEntityMap.get(r.getTarget()).getUUID()));
            sit.addToArgumentList(a);
          } else {
            LOGGER.info("[document={}] Failed to find target entity: {}", id, r.getTarget());
            continue;
          }

          List<Provenance> wInDocProv = r.getProvenances().stream()
              .filter(p -> p.withinDocument(id))
              .collect(Collectors.toList());
          for (Provenance p : wInDocProv) {
            // each prov within this document is a situation mention w/ 1 mention argument
            SituationMention sm = new SituationMention().setUuid(gen.next())
                .setConfidence(r.getConfidence());
            sm.setSituationKind(r.getEvent());
            MentionArgument ma = new MentionArgument();
            TextSpan pts = p.getConcreteStyleTextSpan();
            for (LocalTokenization ltkz : ltkzl) {
              if (ltkz.getTextSpan().overlaps(pts)) {
                ltkz.generateTRS(pts).ifPresent(ma::setTokens);
                break;
              }
            }

            if (!ma.isSetTokens()) {
              LOGGER.info("[document={}] Failed to create supporting TRS for prov: {}", id, p.toString());
              continue;
            }

            sm.addToArgumentList(ma);
            sms.addToMentionList(sm);
            sit.addToMentionIdList(sm.getUuid());
            ss.addToSituationList(sit);
          }
        }

        // next phase - events from this document
        // TODO - more effort needed here
//        if (this.documentIDToEventMap.containsKey(id)) {
//          List<Event> eventList = this.documentIDToEventMap.get(id).stream()
//              .filter(ev -> ev.getDocumentIDs().contains(id))
//              .collect(Collectors.toList());
//
//          for (Event ev : eventList) {
//            Situation sit = new Situation().setUuid(gen.next());
//            // TODO fix
//            sit.setSituationType("EVENT");
//            List<EventPredicate> predicates = ev.getPredicates();
//            for (EventPredicate ep : predicates) {
//              // TODO ???
//              // String agent = ep.getAgent();
//              EntityEventPredicate eep = ep.getPredicate();
//              Relation rel = eep.getRelation();
//              sit.setSituationKind(eep.getRelation().getEvent());
//
//              for (Provenance p : rel.getProvenances()) {
//                MentionArgument ma = new MentionArgument();
//                if (!pToEMIDMap.containsKey(p))
//                  // throw new IllegalArgumentException("failed to create a mention argument for prov: " + p.toString());
//                  LOGGER.warn("[document={}] Failed to create mention argument for prov: {}", p.toString());
//                else {
//                  ma.setEntityMentionId(pToEMIDMap.get(p));
//                  sm.addToArgumentList(ma);
//                }
//              }
//            }
//            sms.addToMentionList(sm);
//          }
//
//          cpy.addToSituationMentionSetList(sms);
//        } else {
//          LOGGER.info("No events for communication: {}", id);
//        }
      }

      if (ems.isSetMentionList())
        cpy.addToEntityMentionSetList(ems);
      if (es.isSetEntityList())
        cpy.addToEntitySetList(es);
      if (sms.isSetMentionList())
        cpy.addToSituationMentionSetList(sms);
      if (ss.isSetSituationList())
        cpy.addToSituationSetList(ss);
    } else {
      LOGGER.info("No entities for communication: {}", id);
    }

    return cpy;
  }

  private EntityMentionSet createEMS(AnalyticUUIDGenerator gen) {
    return new EntityMentionSet()
        .setUuid(gen.next())
        .setMetadata(this.amd);
  }

  private EntitySet createES(AnalyticUUIDGenerator gen) {
    return new EntitySet()
        .setUuid(gen.next())
        .setMetadata(this.amd);
  }

  private SituationMentionSet createSMS(AnalyticUUIDGenerator gen) {
    return new SituationMentionSet()
        .setUuid(gen.next())
        .setMetadata(this.amd);
  }

  private SituationSet createSS(AnalyticUUIDGenerator gen) {
    return new SituationSet()
        .setUuid(gen.next())
        .setMetadata(this.amd);
  }
}
