package edu.jhu.hlt.concrete.services.fetch;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.services.AbstractAuthBasedThriftServiceClient;
import edu.jhu.hlt.concrete.services.ServicesException;

public class FetchTool extends AbstractAuthBasedThriftServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchTool.class);

  private FetchCommunicationService.Client client;

  public FetchTool() throws TTransportException {
    this(ConfigFactory.load());
  }

  public FetchTool(Config cfg) throws TTransportException {
    this(new ConcreteServicesFetchConfig(cfg));
  }

  public FetchTool(ConcreteServicesFetchConfig cfg) throws TTransportException {
    this(cfg.getHost(), cfg.getPort(), cfg.getAuths());
  }

  public FetchTool(String host, int port, String auths) throws TTransportException {
    this(host, port, Optional.ofNullable(auths));
  }

  public FetchTool(String host, int port, Optional<String> auths) throws TTransportException {
    super(host, port, auths);
    LOGGER.debug("Running with host: {}, port: {}, auths: {}", host, port, auths);
    this.client = new FetchCommunicationService.Client(protocol);
  }

  public FetchResult retrieve(Iterable<String> ids) throws ServicesException, TException {
    FetchRequest rr = new FetchRequest()
        .setCommunicationIds(new ArrayList<>());
    this.auths.ifPresent(rr::setAuths);
    ids.forEach(rr::addToCommunicationIds);
    return this.client.fetch(rr);
  }

  private static void toStdout(FetchResult rr) {
    System.out.println("Comm ID\tComm UUID\tText");
    rr.getCommunications().forEach(c -> {
      String text;
      if (c.isSetText()) {
        text = c.getText();
      } else {
        text = "<no text>";
      }
      System.out.print(c.getId());
      System.out.print("\t");
      System.out.print(c.getUuid().getUuidString());
      System.out.print("\t");
      System.out.print(text);
      System.out.println();
    });
  }

  private static void toFile(FetchResult rr) {
    AtomicInteger count = new AtomicInteger(0);
    CompactCommunicationSerializer serializer = new CompactCommunicationSerializer();
    rr.getCommunications().forEach(c -> {
      String filename = c.getId() + ".concrete";
      try (OutputStream os = Files.newOutputStream(Paths.get(filename));
          BufferedOutputStream bos = new BufferedOutputStream(os);) {
        try {
          bos.write(serializer.toBytes(c));
          count.getAndIncrement();
        } catch (Exception e) {
          LOGGER.warn("Caught exception serializing comm " + c.getId(), e);
        }
      } catch (IOException e) {
        LOGGER.warn("Caught IOException: {}", e.getMessage());
      }
    });
    System.out.println("Saved " + count.get() + " concrete files.");
  }

  private static class Opts {
    @Parameter(description = "space separated communication IDs",
        required = true)
    List<String> idList;

    @Parameter(names = { "--file", "-f" },
        description = "Write the comms to file rather than stdout.")
    boolean file = false;

    @Parameter(help = true, names = { "--help", "-h" },
        description = "Print the help message and exit.")
    boolean help = false;
  }

  public static void main(String[] args) {
    Opts opts = new Opts();
    JCommander jc = new JCommander(opts);
    jc.setProgramName("./retrieve.sh");
    try {
      jc.parse(args);
    } catch (ParameterException e) {
      jc.usage();
      System.exit(-1);
    }
    if (opts.help) {
      jc.usage();
      return;
    }

    try (FetchTool tool = new FetchTool();) {
      FetchResult rr = tool.retrieve(opts.idList);
      if (rr.isSetCommunications() && rr.getCommunicationsSize() > 0) {
        if (opts.file) {
          FetchTool.toFile(rr);
        } else {
          FetchTool.toStdout(rr);
        }
      } else {
        System.err.println("Did not retrieve any communications");
      }
    } catch (ServicesException e) {
      LOGGER.warn("Caught services exception: {}", e.getMessage());
    } catch (TException e) {
      LOGGER.warn("Caught TException:", e);
    }
  }
}
