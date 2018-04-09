package edu.jhu.hlt.concrete.services.summarization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.communications.ExampleCommunication;
import edu.jhu.hlt.concrete.services.AbstractThriftServiceClient;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.concrete.summarization.SummarizationRequest;
import edu.jhu.hlt.concrete.summarization.SummarizationService;
import edu.jhu.hlt.concrete.summarization.Summary;
import edu.jhu.hlt.concrete.summarization.SummaryConcept;
import edu.jhu.hlt.concrete.summarization.SummarySourceType;

public class SummarizationTool extends AbstractThriftServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(SummarizationTool.class);

  private final SummarizationService.Client cli;

  SummarizationTool(String host, int port) throws TTransportException {
    super(host, port);
    this.cli = new SummarizationService.Client(this.protocol);
  }

  public Summary summarize(SummarizationRequest sr) throws ServicesException, TException {
    return this.cli.summarize(sr);
  }

  static class Opts {
    @Parameter(description = "Send a simple communication along with the SummarizationRequest")
    boolean withCommunication = false;

    @Parameter(description = "Summarization query", required = true)
    List<String> queryList;

    @Parameter(description = "UUIDs to be used", names = { "-ids", "--uuids" })
    List<String> entityUUIDList = new ArrayList<>();

    @Parameter(names = { "--max-chars", "-mc" }, description = "Max number of characters to return in summary")
    int maxChars = 1000;

    @Parameter(names = { "--max-tokens", "-mt" }, description = "Max number of tokens to return in summary")
    int maxTokens = 100;

    @Parameter(names = { "--type", "-t" }, description = "Type of summarization query (see help string)")
    String type = "document";

    @Parameter(help = true, names = { "--help", "-h" }, description = "Print the help message and exit.")
    boolean help;
  }

  private static void printSummarizationTypes() {
    System.out.println("Supported Summarization types: ");
    for (SummarySourceType st : SummarySourceType.values())
      System.out.println(st.toString());
  }

  public static void main(String... args) {
    Opts opts = new Opts();
    JCommander jc = new JCommander(opts);
    jc.setProgramName("SummarizationTool");
    try {
      jc.parse(args);
    } catch (ParameterException e) {
      jc.usage();
      printSummarizationTypes();
      System.exit(-1);
    }

    if (opts.help) {
      jc.usage();
      printSummarizationTypes();
      return;
    }

    SummarySourceType st;
    try {
      st = SummarySourceType.valueOf(opts.type.toUpperCase(Locale.ENGLISH));
    } catch (IllegalArgumentException ex) {
      System.out.println("Provided summarization type is not supported: " + opts.type + "; see help string");
      return;
    }

    SummarizationRequest sr = new SummarizationRequest();
    sr.setQueryTerms(opts.queryList);
    sr.setMaximumCharacters(opts.maxChars);
    sr.setMaximumTokens(opts.maxTokens);
    sr.setSourceType(st);
    if (opts.withCommunication) {
      ExampleCommunication ex = new ExampleCommunication();
      sr.setSourceCommunication(ex.tokenized());
    }

    for (String id : opts.entityUUIDList)
      sr.addToSourceIds(new edu.jhu.hlt.concrete.UUID(UUID.fromString(id).toString()));

    try (SummarizationTool tool = new ConcreteServicesSummarizationConfig().getSummarizationTool();) {
      Summary s = tool.summarize(sr);
      LOGGER.debug("Retrieved summary: {}", s.toString());
      if (s.isSetConcepts() && s.getConceptsSize() > 0) {
        System.out.println("Concepts");
        System.out.println("Concept\tTokens\tConfidence\tUtility");
        for (SummaryConcept sc : s.getConcepts()) {
          System.out.print(sc.getConcept());
          System.out.print("\t");
          System.out.print(sc.getTokens());
          System.out.print("\t");
          System.out.print(sc.getConfidence());
          System.out.print("\t");
          System.out.print(sc.getUtility());
          System.out.println();
          System.out.println();
        }
      } else
        System.out.println("Service returned no concepts.");

      if (s.isSetSummaryCommunication()) {
        Communication sc = s.getSummaryCommunication();
        if (sc.isSetText()) {
          System.out.println("-----------------");
          System.out.println("Summary");
          System.out.println(sc.getText());
        }
      }
    } catch (TException e) {
      LOGGER.error("Failed to get search result: {}", e.getMessage());
      System.exit(-1);
    }
  }
}
