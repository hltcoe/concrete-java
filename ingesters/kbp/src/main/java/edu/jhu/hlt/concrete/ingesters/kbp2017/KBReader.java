package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.lois.Lois;
import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.channel.impl.BufferedChannel;
import com.flipkart.lois.channel.impl.SimpleChannel;
import com.flipkart.lois.routine.Routine;
import com.google.common.collect.ImmutableMap;

import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

public class KBReader {
  private static final Logger LOGGER = LoggerFactory.getLogger(KBReader.class);

  public static final String STRING_ENTITY_PREFIX = ":String";
  public static final String EVENT_PREFIX = ":Event";
  public static final String ENTITY_PREFIX = ":Entity";

  public static final SubmittedKB createKB(BufferedReader rdr, AnalyticUUIDGenerator gen)
      throws IOException, InterruptedException, KBLineException {

    // maps
    Map<String, Entity> entityMap = new HashMap<>();
    Map<String, StringEntity> seMap = new HashMap<>();
    Map<String, Event> eventMap = new HashMap<>();

    // first line: KB name
    String kbName = rdr.readLine();
    // blank
    rdr.readLine();

    Channel<String[]> stringChannel = new BufferedChannel<>(100);
    Channel<String[]> entityChannel = new BufferedChannel<>(100);
    Channel<String[]> eventChannel = new BufferedChannel<>(100);
    Channel<UUID> uuidC = new SimpleChannel<>();
    // Channel<String[]> provenanceChannel = new BufferedChannel<>(100);

    Routine uuidR = new UUIDGenerationRoutine(uuidC, gen);
    Routine entityRoutine = new EntityProcessingRoutine(entityMap, entityChannel, uuidC);
    Routine stringRoutine = new StringProcessingRoutine(seMap, stringChannel, uuidC);
    Routine eventRoutine = new EventProcessingRoutine(eventMap, eventChannel, uuidC);
    Lois.go(uuidR, entityRoutine, stringRoutine, eventRoutine);

    String line;
    LOGGER.info("Starting to read KB file");
    int ctr = 0;
    try {
      while ((line = rdr.readLine()) != null) {
        String[] split = line.split("\t");
        // if (isProvenanceLine(split)) {
          // provenanceChannel.send(split);
        // }

        String first = split[0];
        if (first.startsWith(STRING_ENTITY_PREFIX)) {
          stringChannel.send(split);
        } else if (first.startsWith(ENTITY_PREFIX)) {
          entityChannel.send(split);
        } else if (first.startsWith(EVENT_PREFIX)) {
          eventChannel.send(split);
        } else {
          stringChannel.close();
          entityChannel.close();
          eventChannel.close();
          uuidC.close();

          throw new IllegalArgumentException("don't know how to handle line: " + line);
        }

        ctr++;
        if (ctr % 100000 == 0) {
          LOGGER.info("Processed {} lines", ctr);
        }
      }

      // send special value as cancel value
      entityChannel.send(new String[0]);
      stringChannel.send(new String[0]);
      eventChannel.send(new String[0]);
    } catch (ChannelClosedException e) {
      // impossible
    }
    LOGGER.info("Done reading; processed {} lines", ctr);

    stringChannel.close();
    entityChannel.close();
    eventChannel.close();
    uuidC.close();
    // provenanceChannel.close();

    // let the last things be added
    Thread.sleep(1000L);

    return new SubmittedKB.Builder()
        .setKBName(kbName)
        .putAllEntityMap(ImmutableMap.copyOf(entityMap))
        .putAllStringEntityMap(ImmutableMap.copyOf(seMap))
        .putAllEventMap(ImmutableMap.copyOf(eventMap))
        .build();
  }

  private static class Opts {
    @ParametersDelegate
    KBParameterDelegate kbParams = new KBParameterDelegate();

    @Parameter(description = "Path to the output file",
        names = {"--output-path", "-out"})
    String outPathStr = "entities.json.gz";

    @Parameter(help = true, names = {"--help", "-h"},
        description = "Print the help text and exit")
    boolean help;
  }

  public static void main(String... args) {
    Opts o = new Opts();
    JCommander jc  = JCommander.newBuilder().addObject(o).build();
    jc.parse(args);
    if (o.help) {
      jc.usage();
      return;
    }

    Path input = o.kbParams.kbPath;
    if (!Files.exists(input)) {
      System.out.println("Input file does not exist");
      System.exit(1);
    }

    Path outPath = Paths.get(o.outPathStr);
    if (Files.exists(outPath)) {
      System.out.println("Output path already exists, not overwriting");
      System.exit(1);
    }

    boolean failed = false;
    ObjectMapper om = new ObjectMapper();
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    try (BufferedReader rdr = o.kbParams.getReader();) {
      SubmittedKB skb = createKB(rdr, g);
      LOGGER.info("KB created successfully");
      try(OutputStream os = Files.newOutputStream(outPath);
          BufferedOutputStream bout = new BufferedOutputStream(os);
          GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bout);
          OutputStreamWriter osw = new OutputStreamWriter(gout, StandardCharsets.UTF_8);
          BufferedWriter bw = new BufferedWriter(osw);) {
        // done so as to more easily inspect output file
        om.writerWithDefaultPrettyPrinter().writeValue(bw, skb);
      }
      LOGGER.info("Entities serialized successfully");
    } catch (IOException | InterruptedException | KBLineException e) {
      LOGGER.error("Exception parsing file", e);
      failed = true;
    }

    if (failed)
      System.exit(2);
  }
}
