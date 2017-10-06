package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.inferred.freebuilder.shaded.com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import edu.jhu.hlt.concrete.ingesters.kbp2017.Entity;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Mention;
import edu.jhu.hlt.concrete.ingesters.kbp2017.Provenance;
import edu.jhu.hlt.concrete.ingesters.kbp2017.SubmittedKB;
import edu.jhu.hlt.concrete.ingesters.kbp2017.TextSpan;

public class DBCreationPhaseTwo {

  private static final Logger LOGGER = LoggerFactory.getLogger(DBCreationPhaseTwo.class);

  private static final String entitySQL =
      "INSERT INTO entity_mentions(uuid, entities_id, comms_id,"
      + " start, end, type, txt)"
      + " VALUES (?, ?, ?, ?, ?, ?, ?)";

  private static class Opts {
    @Parameter(description = "Path to the output SQLite DB file", names = {"--db-file", "-db"})
    String dbPathStr = "tackbp";

    @Parameter(description = "Path to the KB File", names = {"--kb-path", "-kb"},
        required = true)
    String kbPathStr;

    @Parameter(help = true, names = {"--help", "-h"},
        description = "Print the help string and exit")
    boolean help;
  }

  public static void main(String[] args) {
    Opts o = new Opts();
    JCommander jc = new JCommander.Builder().addObject(o).build();
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
      System.out.println("Input file does not exist");
      System.exit(1);
    }

    Path dbPath = Paths.get(o.dbPathStr + ".log");
    if (!Files.exists(dbPath)) {
      System.out.println("DB file does not exist");
      System.exit(1);
    }

    try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + o.dbPathStr, "sa", "");) {
      try (Statement statement = c.createStatement();) {
        statement.execute("SET DATABASE SQL SYNTAX PGS TRUE");
        statement.executeUpdate("DROP TABLE IF EXISTS entity_mentions");
        statement.executeUpdate("DROP TABLE IF EXISTS entities");

        statement.executeUpdate("CREATE TABLE entities (id text PRIMARY KEY NOT NULL, uuid text UNIQUE)");
        statement.executeUpdate("CREATE TABLE entity_mentions (uuid text PRIMARY KEY NOT NULL,"
            + " entities_id text NOT NULL,"
            + " comms_id text NOT NULL,"
            + " start integer NOT NULL, end integer NOT NULL, type text, txt text,"
            + " FOREIGN KEY (entities_id) REFERENCES entities(id),"
            + " FOREIGN KEY (comms_id) REFERENCES comms(id))");
        statement.executeUpdate("CREATE INDEX idx_entity_mentions_entities_id ON entity_mentions(entities_id)");
        statement.executeUpdate("CREATE INDEX idx_entity_mentions_comms_id ON entity_mentions(comms_id)");
        statement.executeUpdate("CREATE INDEX idx_entity_mentions_start ON entity_mentions(start)");
        statement.executeUpdate("CREATE INDEX idx_entity_mentions_end ON entity_mentions(end)");
      }

      ImmutableSet.Builder<String> b = ImmutableSet.builder();
      try (PreparedStatement statement = c.prepareStatement("SELECT id FROM comms");) {
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
          b.add(rs.getString("id"));
        }
      }

      ImmutableSet<String> commIDs = b.build();
      LOGGER.info("Document set: {} items", commIDs.size());
      c.setAutoCommit(false);
      try (InputStream in = Files.newInputStream(kbPath);
          BufferedInputStream bin = new BufferedInputStream(in, 1024 * 32);
          GzipCompressorInputStream gin = new GzipCompressorInputStream(bin);) {
        SubmittedKB skb = ConcreteSubmittedKB.readSubmittedKB(gin);
        LOGGER.info("KB deserialized OK");
        int nProcessed = 0;
        try (PreparedStatement ps = c.prepareStatement(entitySQL);) {
          for (Entity e : skb.getEntityMap().values()) {
            try (Statement statement = c.createStatement();) {
              statement.executeUpdate("INSERT INTO entities VALUES('"
                  + e.getID() + "', '" + e.getUUID().toString() + "')");
              for (Mention m : e.getMentions()) {
                Provenance p = m.getProvenance();
                if (!commIDs.contains(p.getDocumentID())) {
                  LOGGER.info("Provenance not found in document set: {}", p.toString());
                  continue;
                }

                TextSpan ts = p.getTextSpan();
                ps.setString(1, m.getUUID().toString());
                ps.setString(2, e.getID());
                ps.setString(3, p.getDocumentID());
                ps.setInt(4, ts.getStart());
                ps.setInt(5, ts.getEnd());
                ps.setString(6, m.getType().toString());
                ps.setString(7, m.getText());
                ps.execute();
              }
            }

            nProcessed++;
            if (nProcessed % 1000 == 0)
              LOGGER.info("Processed {} entities", nProcessed);
          }
        }
      } catch (IOException e1) {
        LOGGER.error("IOEx during processing", e1);
      }

//      c.commit();

//      try (InputStream cin = Files.newInputStream(commsPath);
//          BufferedInputStream cbin = new BufferedInputStream(cin, 1024 * 32);
//          TarGzArchiveEntryCommunicationIterator iter = new TarGzArchiveEntryCommunicationIterator(cbin);) {
//        int nProcessed = 0;
//        while (iter.hasNext()) {
//          try (Statement statement = c.createStatement();) {
//            Communication comm = iter.next();

//            LocalCommunication lc = new LocalCommunication(comm);
//            for (Sentence st : lc.getSentences()) {
//              statement.executeUpdate("INSERT INTO sentences VALUES('"
//                  + st.getUuid().getUuidString() + "', '"
//                  + comm.getId() + "', "
//                  + st.getTextSpan().getStart() + ", "
//                  + st.getTextSpan().getEnding()
//                  + ")");
//            }
//
//            nProcessed++;
//          }
//
//          if (nProcessed % 1000 == 0) {
//            LOGGER.info("Progress: {}", nProcessed);
//          }
//        }
//      } catch (IOException e) {
//        LOGGER.error("IOException opening file", e);
//      }

      LOGGER.info("Committing");
      c.commit();
      LOGGER.info("Done");
    } catch (SQLException e) {
      LOGGER.error("Caught SQL exception", e);
    }
  }
}
