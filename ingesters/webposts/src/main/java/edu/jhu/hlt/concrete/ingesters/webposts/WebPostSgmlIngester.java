package edu.jhu.hlt.concrete.ingesters.webposts;

import java.io.BufferedReader;
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

/**
 * Performs ingestion on one Webpost SGML file.
 *
 * @author Tongfei Chen
 */
public class WebPostSgmlIngester {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebPostGzIngester.class);

  public static void main(String[] args) throws Exception {
    WebPostIngester wpi = new WebPostIngester();

    CompactCommunicationSerializer ser = new CompactCommunicationSerializer();

    String inputSgmlFilename = args[0];
    String outputDir = args[1];

    try (BufferedReader is = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(inputSgmlFilename)), StandardCharsets.UTF_8));) {
      PrintWriter pw = null;
      String id = "";
      String line = "";

      while ((line = is.readLine()) != null) {

        if (line.startsWith("<DOC>"))
          continue;

        if (line.startsWith("<DOCID>")) {
          id = line.substring("<DOCID>".length(), line.length() - "</DOCID>".length()).trim();
          pw = new PrintWriter(id, StandardCharsets.UTF_8.toString());
          pw.println("<DOC>");
          pw.println(line);
        } else if (line.equals("\">")) {
          pw.println("\"/>"); // !!! fix Webposts <QUOTE> tag unclosed bug by
                              // manually closing them!
        } else
          pw.println(WebPostGzIngester.escapeAmpersands(line)); // !!! fix
                                                                // unescaped XML
                                                                // ampersand
                                                                // symbol!

        if (line.equals("</DOC>")) {
          pw.close();
          try {
            Communication comm = wpi.fromCharacterBasedFile(Paths.get(id));
            try (OutputStream os = Files.newOutputStream(Paths.get(outputDir + "/" + comm.getId() + ".comm"));) {
              os.write(ser.toBytes(comm));
            }

            LOGGER.info("Processed file " + comm.getId());
          } catch (IngestException e) {
            LOGGER.error("Error processing communication " + id, e);
          }
          Files.delete(Paths.get(id));
        }
      }
    }
  }
}
