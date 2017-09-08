package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.lois.Lois;
import com.flipkart.lois.channel.api.Channel;
import com.flipkart.lois.channel.exceptions.ChannelClosedException;
import com.flipkart.lois.channel.impl.BufferedChannel;
import com.flipkart.lois.routine.Routine;
import com.google.common.collect.ImmutableMap;

public class KBReader {
  private static final Logger LOGGER = LoggerFactory.getLogger(KBReader.class);

  public static final String STRING_ENTITY_PREFIX = ":String";
  public static final String EVENT_PREFIX = ":Event";
  public static final String ENTITY_PREFIX = ":Entity";

  public static final SubmittedKB createKB(BufferedReader rdr)
      throws IOException, InterruptedException, KBLineException {

    // maps
    Map<String, Entity> entityMap = new HashMap<>();
    Map<String, StringEntity> seMap = new HashMap<>();

    // first line: KB name
    String kbName = rdr.readLine();
    // blank
    rdr.readLine();

    Channel<String[]> stringChannel = new BufferedChannel<>(100);
    Channel<String[]> entityChannel = new BufferedChannel<>(100);
    // Channel<String[]> eventChannel = new BufferedChannel<>(100);
    // Channel<String[]> provenanceChannel = new BufferedChannel<>(100);

    Routine entityRoutine = new EntityProcessingRoutine(entityMap, entityChannel);
    Routine stringRoutine = new StringProcessingRoutine(seMap, stringChannel);
    Lois.go(entityRoutine, stringRoutine);

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
          // eventChannel.send(split);
        } else {
          stringChannel.close();
          entityChannel.close();
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
    } catch (ChannelClosedException e) {
      // impossible
    }
    LOGGER.info("Done reading; processed {} lines", ctr);

    stringChannel.close();
    entityChannel.close();
    // eventChannel.close();
    // provenanceChannel.close();

    // let the last things be added
    Thread.sleep(1000L);

    return new SubmittedKB.Builder()
        .setKBName(kbName)
        .putAllEntityMap(ImmutableMap.copyOf(entityMap))
        .putAllStringEntityMap(ImmutableMap.copyOf(seMap))
        .build();
  }

  private static class Opts {
    @Parameter(description = "Path to the KB File", names = {"--path", "-p"})
    String kbPathStr;

    @Parameter(help = true, names = {"--help", "-h"})
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

    Path input = Paths.get(o.kbPathStr);
    if (!Files.exists(input)) {
      System.out.println("Input file does not exist");
      System.exit(1);
    }

    boolean failed = false;
    try (InputStream in = Files.newInputStream(input);
        BufferedInputStream bin = new BufferedInputStream(in, 1024 * 32);
        GzipCompressorInputStream gin = new GzipCompressorInputStream(bin);
        InputStreamReader irdr = new InputStreamReader(gin, StandardCharsets.UTF_8);
        BufferedReader rdr = new BufferedReader(irdr);) {
      SubmittedKB skb = createKB(rdr);
      LOGGER.info("KB created successfully");
      ObjectMapper om = new ObjectMapper();
      try(BufferedWriter bw = Files.newBufferedWriter(Paths.get("entities.json"), StandardCharsets.UTF_8)) {
        om.writeValue(bw, skb);
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
