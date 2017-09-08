package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

public class KBOps {

  private static final Logger LOGGER = LoggerFactory.getLogger(KBOps.class);

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
    ObjectMapper om = new ObjectMapper();
    try (InputStream in = Files.newInputStream(input);
        BufferedInputStream bin = new BufferedInputStream(in, 1024 * 32);
        GzipCompressorInputStream gin = new GzipCompressorInputStream(bin);
        InputStreamReader irdr = new InputStreamReader(gin, StandardCharsets.UTF_8);
        BufferedReader rdr = new BufferedReader(irdr);) {
      SubmittedKB skb = om.readValue(rdr, SubmittedKB.class);
      LOGGER.info("KB deserialized OK");
      List<Entity> eList = ImmutableList.copyOf(skb.getEntityMap().values());
      for (int i = 0; i < 10; i++) {
        Entity e = eList.get(0);
        // show all relationships w/ other entity targets
        Set<String> connectedEntities = e.getRelations().stream()
            .map(Relation::getTarget)
            .collect(Collectors.toSet());
        connectedEntities.forEach(le -> LOGGER.info("Entity {} is connected to entity {}", e.getID(), le));
      }
    } catch (IOException e) {
      LOGGER.error("Exception parsing file", e);
      failed = true;
    }

    if (failed)
      System.exit(2);
  }
}
