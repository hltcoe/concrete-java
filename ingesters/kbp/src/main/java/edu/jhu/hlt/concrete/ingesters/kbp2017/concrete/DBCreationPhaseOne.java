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
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.serialization.iterators.TarGzArchiveEntryCommunicationIterator;

public class DBCreationPhaseOne {

  private static final Logger LOGGER = LoggerFactory.getLogger(DBCreationPhaseOne.class);

  private static final String commSQL =
      "INSERT INTO comms(id, uuid) VALUES (?, ?)";
  private static final String sentSQL =
      "INSERT INTO sentences(uuid, comms_id, start, end) VALUES (?, ?, ?, ?)";

  private static class Opts {
    @Parameter(description = "Path to the communications .tar.gz file",
        names = {"--comms-path", "-in"}, required = true)
    String pathToCommsTarGZ;

    @Parameter(description = "Path to the output SQLite DB file", names = {"--db-file", "-db"})
    String dbPathStr = "tackbp";

    @Parameter(help = true, names = {"--help", "-h"},
        description = "Print the help string and exit")
    boolean help;
  }

  public static void main(String[] args) {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
    } catch (Exception e) {
      LOGGER.error("Failed to load HSQLDB driver", e);
      System.exit(128);
    }

    Opts o = new Opts();
    JCommander jc = new JCommander.Builder().addObject(o).build();
    jc.parse(args);

    if (o.help) {
      jc.usage();
      return;
    }

    Path commsPath = Paths.get(o.pathToCommsTarGZ);
    if (!Files.exists(commsPath)) {
      System.out.println("Comms .tar.gz file does not exist");
      System.exit(1);
    }

    Path outPath = Paths.get(o.dbPathStr);
    if (Files.exists(outPath)) {
      System.out.println("File already exists at DB path; not overwriting");
      System.exit(1);
    }

    try(Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + o.dbPathStr, "sa", "");) {
      try (Statement statement = c.createStatement();) {
        statement.execute("SET DATABASE SQL SYNTAX PGS TRUE");

        statement.executeUpdate("DROP TABLE IF EXISTS sentences");
        statement.executeUpdate("DROP TABLE IF EXISTS comms");

        statement.executeUpdate("CREATE TABLE comms (id text PRIMARY KEY NOT NULL, uuid text UNIQUE)");
        statement.executeUpdate("CREATE TABLE sentences (uuid text PRIMARY KEY NOT NULL,"
            + " comms_id text NOT NULL,"
            + " start integer NOT NULL,"
            + " end integer NOT NULL,"
            + " FOREIGN KEY (comms_id) REFERENCES comms(id))");
        statement.executeUpdate("CREATE INDEX idx_sentences_comms_id ON sentences(comms_id)");
        statement.executeUpdate("CREATE INDEX idx_sentences_start ON sentences(start)");
        statement.executeUpdate("CREATE INDEX idx_sentences_end ON sentences(end)");
      }

      c.setAutoCommit(false);
      try (PreparedStatement commPS = c.prepareStatement(commSQL);
          PreparedStatement sentPS = c.prepareStatement(sentSQL);) {
        try (InputStream cin = Files.newInputStream(commsPath);
            BufferedInputStream cbin = new BufferedInputStream(cin, 1024 * 32);
            TarGzArchiveEntryCommunicationIterator iter = new TarGzArchiveEntryCommunicationIterator(cbin);) {
          int nProcessed = 0;
          while (iter.hasNext()) {
            Communication comm = iter.next();
            commPS.setString(1, comm.getId());
            commPS.setString(2, comm.getUuid().getUuidString());
            commPS.execute();
            LocalCommunication lc = new LocalCommunication(comm);
            for (Sentence st : lc.getSentences()) {
              sentPS.setString(1, st.getUuid().getUuidString());
              sentPS.setString(2, comm.getId());
              sentPS.setInt(3, st.getTextSpan().getStart());
              sentPS.setInt(4, st.getTextSpan().getEnding());
              sentPS.execute();
            }

            nProcessed++;

            if (nProcessed % 1000 == 0) {
              LOGGER.info("Progress: {}", nProcessed);
            }
          }
        } catch (IOException e) {
          LOGGER.error("IOException opening file", e);
        }
      }
      // ResultSet rs = statement.executeQuery("select * from person");


      LOGGER.info("Committing");
      c.commit();
      LOGGER.info("Done");
    } catch (SQLException e) {
      LOGGER.error("Caught SQL exception", e);
    }
  }
}
