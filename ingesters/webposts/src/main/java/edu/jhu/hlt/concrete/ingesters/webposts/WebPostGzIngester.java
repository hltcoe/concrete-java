package edu.jhu.hlt.concrete.ingesters.webposts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;

/**
 * Performs ingestion on one Webpost gz file.
 *
 * @author Tongfei Chen
 */
public class WebPostGzIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebPostGzIngester.class);

  static String escapeAmpersands(String x) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < x.length(); i++) {
      if (x.charAt(i) != '&')
        sb.append(x.charAt(i));
      else {

        int semicolonPos = x.indexOf(';', i);
        if (semicolonPos == -1 || semicolonPos - i > 4)
          sb.append("&amp;");
        else {

          String escapePattern = x.substring(i + 1, semicolonPos);
          if (escapePattern.equals("amp") || escapePattern.equals("quot") || escapePattern.equals("lt")
              || escapePattern.equals("gt"))
            sb.append('&');
          else
            sb.append("&amp;");
        }
      }
    }
    return sb.toString();
  }

  public static void main(String[] args) throws Exception {

    WebPostIngester wpi = new WebPostIngester();

    CompactCommunicationSerializer ser = new CompactCommunicationSerializer();

    String inputGzFilename = args[0];
    String outputDir = args[1];

    BufferedReader is = new BufferedReader(
        new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(inputGzFilename))), StandardCharsets.UTF_8));

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
        pw.println(escapeAmpersands(line)); // !!! fix unescaped XML ampersand
                                            // symbol!

      if (line.equals("</DOC>")) {
        pw.close();
        try {
          Communication comm = wpi.fromCharacterBasedFile(Paths.get(id));

          OutputStream os = Files.newOutputStream(Paths.get(outputDir + "/" + comm.getId() + ".comm"));
          os.write(ser.toBytes(comm));
          os.close();

          LOGGER.info("Processed file " + comm.getId());
        } catch (IngestException e) {
          LOGGER.error("Error processing communication " + id, e);
        }
        Files.delete(Paths.get(id));
      }

    }
    is.close();
  }

}
