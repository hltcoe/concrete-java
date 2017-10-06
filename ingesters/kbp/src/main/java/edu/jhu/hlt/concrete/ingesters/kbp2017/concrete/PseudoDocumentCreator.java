/**
 *
 */
package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Entity;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Mention;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Provenance;
import edu.jhu.hlt.concrete.ingesters.kbp2017.TextSpan;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadataFactory;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.concrete.serialization.iterators.TarGzArchiveEntryCommunicationIterator;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;



/**
 *
 */
public class PseudoDocumentCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(PseudoDocumentCreator.class);
  private final Map<String, LocalCommunication> idToCommMap;
  private final ConcreteSubmittedKB ckb;

  private static final String sentenceSelectSQL =
      "SELECT uuid, start, end FROM sentences WHERE comms_id = ? AND "
      + " start <= ? AND end >= ?";
  private static final String selectEMSQL = "SELECT uuid, entities_id AS eid,"
      + " start, end,"
      + " type AS t, txt"
      + " FROM entity_mentions AS em"
      + " WHERE em.comms_id = ? AND em.start >= ? AND em.end <= ?";

  public PseudoDocumentCreator(Map<String, LocalCommunication> commMap, ConcreteSubmittedKB ckb) {
    this.ckb = ckb;
    this.idToCommMap = commMap;
  }

  private static class Opts {
    @Parameter(description = "Path to the communications .tar.gz file",
        names = {"--comms-path", "-in"}, required = true)
    String pathToCommsTarGZ;

    @Parameter(description = "Path to the KB File", names = {"--kb-path", "-kb"},
        required = true)
    String kbPathStr;

    @Parameter(description = "Path to the output DB", names = {"--db-file", "-db"})
    String dbPathStr = "tackbp";

    @Parameter(description = "Path to the output file", names = {"--output-path", "-out"})
    String outputPathStr = "synthetic-entity-comms.tar.gz";

    @Parameter(help = true, names = {"--help", "-h"},
        description = "Print the help string and exit")
    boolean help;
  }

  public void createPseudoDocuments(TarArchiver arch, Connection c) {
    AnnotationMetadata amd = AnnotationMetadataFactory.fromCurrentLocalTime("PseudoDocumentCreator");
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator gen = f.create();

    try (PreparedStatement sentencePS = c.prepareStatement(sentenceSelectSQL);
        PreparedStatement emPS = c.prepareStatement(selectEMSQL);) {
      for (Entity e : this.ckb.idToEntityMap().values()) {
        Communication synth = new Communication()
            .setUuid(UUIDFactory.fromJavaUUID(e.getUUID()))
            .setMetadata(amd)
            .setType("entity-synthetic-communication")
            .setId(e.getID());
        Section toAdd = new Section()
            .setUuid(gen.next())
            .setKind("sentence-holder");

        EntityMentionSet ems = new EntityMentionSet().setUuid(gen.next())
            .setMetadata(amd);
        EntitySet origES = new EntitySet().setUuid(gen.next())
            .setMetadata(amd);

        // for part 2 - add the entity mentions from other sents
        // set of entity IDs to batch add
        Set<String> targetEntityIDs = new HashSet<>();
        AnnotationMetadata othersAM = AnnotationMetadataFactory.fromCurrentLocalTime("PseudoDocumentsOtherDocs");
        EntityMentionSet otherSentsEMS = new EntityMentionSet().setUuid(gen.next())
            .setMetadata(othersAM);
        EntitySet otherSentsES = new EntitySet().setUuid(gen.next())
            .setMetadata(othersAM);

        edu.jhu.hlt.concrete.Entity concE = e.toConcrete();
        List<Mention> xdocMentions = e.getMentions();
        // drop mentions w/ duplicate provenances
        Map<Provenance, Mention> tsToMentionMap = new HashMap<>();
        xdocMentions.forEach(m -> {
          Provenance mp = m.getProvenance();
          if (!tsToMentionMap.containsKey(mp))
            tsToMentionMap.put(mp, m);
        });
        for (Mention m : tsToMentionMap.values()) {
          EntityMention em = new EntityMention()
              .setUuid(gen.next())
              .setText(m.getText())
              .setPhraseType(m.getType().toString());
          Provenance p = m.getProvenance();
          TextSpan lts = p.getConcreteStyleTextSpan();

          // TODO
          // plug in SM stuff here / resolve
//          if (provToIDMap.containsKey(p))
//            LOGGER.debug("[document={}] Duplicate provenance: {}", id, p.toString());
//          else
//            provToIDMap.put(p, em.getUuid());
          // TODO plug token alignment
          String docID = m.getProvenance().getDocumentID();
          if (!this.idToCommMap.containsKey(docID)) {
            LOGGER.info("Missing doc: {}", docID);
            continue;
          }
          LocalCommunication target = this.idToCommMap.get(docID);
          Optional<Sentence> mentionSent = target.getSentence(lts.toConcrete());
          if (!mentionSent.isPresent()) {
            LOGGER.warn("[doc={}] Sentence was not found: for mention {}", docID, lts.toString());
            continue;
          }

          mentionSent.ifPresent(toAdd::addToSentenceList);
          Map<UUID, LocalTokenization> sentToTKZMap = target.sentenceIDToTokenizationMap();
          for (LocalTokenization ltkz : sentToTKZMap.values()) {
            if (ltkz.getTextSpan().overlaps(lts)) {
              ltkz.generateTRS(lts).ifPresent(em::setTokens);
              break;
            }
          }

          if (!em.isSetTokens()) {
            LOGGER.info("[doc={}] Failed to find supporting tokens for mention: {}", docID, m.toString());
            continue;
          }

          ems.addToMentionList(em);
          concE.addToMentionIdList(em.getUuid());

          ////////////////////////////////////////////////////////
          // phase 2 - get Sentence w/ this EntityMention
          ////////////////////////////////////////////////////////
          UUID sentUUID;
          sentencePS.setString(1, docID);
          sentencePS.setInt(2, lts.getStart());
          sentencePS.setInt(3, lts.getEnd());
          TextSpan targetSentenceTS;
          try (ResultSet rs = sentencePS.executeQuery();) {
            if (rs.next()) {
              String sentenceUUIDString = rs.getString("uuid");
              int sentenceStart = rs.getInt("start");
              int sentenceEnd = rs.getInt("end");
              targetSentenceTS = TextSpan.create(sentenceStart, sentenceEnd);
              sentUUID = UUID.fromString(sentenceUUIDString);
            } else {
              LOGGER.warn("Mention not found in DB: {}", m.toString());
              continue;
            }
          }

          LocalTokenization sentenceTKZ = sentToTKZMap.get(sentUUID);
          // have sentence UUID, now get list of mentions in this sentence
          emPS.setString(1, docID);
          emPS.setInt(2, targetSentenceTS.getStart());
          emPS.setInt(3, targetSentenceTS.getEnd());

          try (ResultSet rs = emPS.executeQuery();) {
            while (rs.next()) {
              UUID mentionUUID = UUID.fromString(rs.getString("uuid"));
              String eid = rs.getString("eid");
              int mentionStart = rs.getInt("start");
              int mentionEnd = rs.getInt("end");
              Optional<String> eType = Optional.ofNullable(rs.getString("t"));
              Optional<String> eText= Optional.ofNullable(rs.getString("txt"));
              EntityMention sqlEM = new EntityMention()
                  .setUuid(UUIDFactory.fromJavaUUID(mentionUUID));
              eType.ifPresent(sqlEM::setPhraseType);
              eText.ifPresent(sqlEM::setText);

              // +1 bc raw data is not concrete style TS
              TextSpan sqlMenTS = TextSpan.create(mentionStart, mentionEnd + 1);
              Optional<TokenRefSequence> trs = sentenceTKZ.generateTRS(sqlMenTS);
              if (!trs.isPresent()) {
                LOGGER.warn("Failed to map tokens for SQL text span: {}", sqlMenTS.toString());
                continue;
              }

              trs.ifPresent(sqlEM::setTokens);
              otherSentsEMS.addToMentionList(sqlEM);
              targetEntityIDs.add(eid);
            }
          }
        }

        if (!ems.isSetMentionList()) {
          LOGGER.warn("[entity={}] Failed to create any EntityMentions for entity: {}", e.getID(), e.getUUID());
          continue;
        } else
          synth.addToEntityMentionSetList(ems);

        if (!concE.isSetMentionIdList()) {
          LOGGER.warn("[entity={}] Entity had no EntityMentions: {}", e.getID(), e.getUUID());
          continue;
        } else {
          origES.addToEntityList(concE);
          synth.addToEntitySetList(origES);
        }

        if (!otherSentsEMS.isSetMentionList()) {
          LOGGER.warn("[entity={}] Failed to create any sentence-style EntityMentions for entity: {}", e.getID(), e.getUUID());
          continue;
        } else {
          synth.addToEntityMentionSetList(otherSentsEMS);
          for (String s : targetEntityIDs) {
            Entity ent = this.ckb.idToEntityMap().get(s);
            if (ent == null)
              throw new RuntimeException("no");
            edu.jhu.hlt.concrete.Entity cEnt = ent.toConcrete();
            ent.getMentions().stream()
              .map(Mention::getUUID)
              .map(UUIDFactory::fromJavaUUID)
              .forEach(cEnt::addToMentionIdList);
          }
          targetEntityIDs.stream()
            .map(s -> this.ckb.idToEntityMap().get(s))
            .map(Entity::toConcrete)
            .map(ent -> ent.setMentionIdList(ImmutableList.of()))
            .forEach(otherSentsES::addToEntityList);
          if (!otherSentsES.isSetEntityList()) {
            LOGGER.warn("[entity={}] Failed to create any sentence-style Entities for entity: {}", e.getID(), e.getUUID());
          } else
            synth.addToEntitySetList(otherSentsES);
        }

        synth.addToSectionList(toAdd);
        try {
          arch.addEntry(new ArchivableCommunication(synth));
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    } catch (SQLException sqle) {
      LOGGER.error("SQLException", sqle);
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Opts o = new Opts();
    JCommander jc = JCommander.newBuilder().addObject(o).build();
    jc.parse(args);
    if (o.help) {
      jc.usage();
      return;
    }

    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (Exception e) {
      LOGGER.error("Failed to load HSQLDB driver", e);
      System.exit(128);
    }

    Path kbPath = Paths.get(o.kbPathStr);
    if (!Files.exists(kbPath)) {
      System.out.println("Input KB file does not exist");
      System.exit(1);
    }

    Path outPath = Paths.get(o.outputPathStr);
    if (Files.exists(outPath)) {
      System.out.println("Output path already exists, not overwriting");
      System.exit(1);
    }

    Path dbPath = Paths.get(o.dbPathStr + ".log");
    if (!Files.exists(dbPath)) {
      System.out.println("DB file does not exist");
      System.exit(1);
    }

    Path commsPath = Paths.get(o.pathToCommsTarGZ);
    if (!Files.exists(commsPath)) {
      System.out.println("Comms .tar.gz file does not exist");
      System.exit(1);
    }

    AtomicBoolean failed = new AtomicBoolean(false);
    ListeningExecutorService exe = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    Callable<ConcreteSubmittedKB> loadKB = () -> {
      try (InputStream in = Files.newInputStream(kbPath);
          BufferedInputStream bin = new BufferedInputStream(in, 1024 * 32);
          GzipCompressorInputStream gin = new GzipCompressorInputStream(bin);) {
        return new ConcreteSubmittedKB(gin);
      } catch (IOException ex) {
        LOGGER.error("Exception during KB load: {}", ex);
        failed.set(false);
        throw new IllegalArgumentException("foo");
      }
    };

    Map<String, LocalCommunication> comms = new HashMap<>(30000);
    try (InputStream in = Files.newInputStream(commsPath);
        BufferedInputStream bin = new BufferedInputStream(in, 1024 * 32);
        TarGzArchiveEntryCommunicationIterator iter = new TarGzArchiveEntryCommunicationIterator(bin);) {

      while (iter.hasNext()) {
        Communication lc = iter.next();
        comms.put(lc.getId(), new LocalCommunication(lc));
      }
    } catch (IOException ex) {
      LOGGER.error("Exception during KB load: {}", ex);
      failed.set(true);
      throw new IllegalArgumentException("foo");
    }


    ListenableFuture<ConcreteSubmittedKB> kbf = exe.submit(loadKB);
    try {
      ConcreteSubmittedKB ckb = kbf.get();
      PseudoDocumentCreator creator = new PseudoDocumentCreator(comms, ckb);
      try (Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + o.dbPathStr, "sa", "");) {
        try (Statement st = c.createStatement();) {
          st.execute("SET DATABASE SQL SYNTAX PGS TRUE");
        }

        try (OutputStream os = Files.newOutputStream(outPath);
            BufferedOutputStream bos = new BufferedOutputStream(os, 1024 * 16);
            GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bos);
            TarArchiver arch = new TarArchiver(gout);) {
          LOGGER.info("Beginning processing");
          // TODO
          creator.createPseudoDocuments(arch, c);
          LOGGER.info("Done");
        } catch (IOException e) {
          LOGGER.error("Exception parsing file", e);
          failed.set(true);
        }
      } catch (SQLException e1) {
        LOGGER.error("SQLEx", e1);
      }
    } catch (InterruptedException | ExecutionException e2) {
      LOGGER.error("Exception", e2);
    }

    exe.shutdown();
    try {
      exe.awaitTermination(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
    } catch (InterruptedException ex) {

    }
    if (failed.get())
      System.exit(2);
  }
}
