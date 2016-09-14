package edu.jhu.hlt.concrete.ingesters.bolt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 *
 * Performs ingestion on one BOLT SGML file.
 *
 * By Tongfei Chen
 *
 */
public class BoltSgmlIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(BoltGzIngester.class);

  public static void main(String[] args) throws IOException, IngestException, ConcreteException {

    BoltForumPostIngester bfpi = new BoltForumPostIngester();

    CompactCommunicationSerializer ser = new CompactCommunicationSerializer();

    String inputSgmlFilename = args[0];
    String outputDir = args[1];

    try (BufferedReader is = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(inputSgmlFilename)), StandardCharsets.UTF_8));) {
      PrintWriter pw = new PrintWriter("temp", StandardCharsets.UTF_8.toString());
      String id = "";
      String line = "";

      while ((line = is.readLine()) != null) {

        if (line.startsWith("<doc")) {
          id = line.substring("<doc id=\"".length(), line.length() - "\">".length());
        }

        pw.println(line);

        if (line.equals("</doc>")) {
          pw.close();
          Files.move(Paths.get("temp"), Paths.get(outputDir + "/" + id));
          try {
            Communication comm = bfpi.fromCharacterBasedFile(Paths.get(outputDir + "/" + id));
            try (OutputStream os = Files.newOutputStream(Paths.get(outputDir + "/" + comm.getId() + ".comm"));) {
              os.write(ser.toBytes(comm));
            }

            LOGGER.info("Processed file " + comm.getId());
          } catch (IngestException e) {
            LOGGER.error("Error processing communication" + id, e);
          }
          Files.delete(Paths.get(outputDir + "/" + id));
        }
      }
    }
  }
}
