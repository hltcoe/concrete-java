package edu.jhu.hlt.concrete.services.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.search.SearchQuery;
import edu.jhu.hlt.concrete.search.SearchResult;
import edu.jhu.hlt.concrete.search.SearchResultItem;
import edu.jhu.hlt.concrete.search.SearchService;
import edu.jhu.hlt.concrete.search.SearchType;
import edu.jhu.hlt.concrete.services.AbstractAuthBasedThriftServiceClient;
import edu.jhu.hlt.concrete.services.ServicesException;

public class SearchTool extends AbstractAuthBasedThriftServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchTool.class);

  private final SearchService.Client client;

  public SearchTool() throws TTransportException {
    this(ConfigFactory.load());
  }

  public SearchTool(Config cfg) throws TTransportException {
    this(new ConcreteServicesSearchConfig(cfg));
  }

  public SearchTool(ConcreteServicesSearchConfig cfg) throws TTransportException {
    this(cfg.getHost(), cfg.getPort(), cfg.getAuths());
  }

  public SearchTool(String host, int port, String auths) throws TTransportException {
    this(host, port, Optional.ofNullable(auths));
  }

  public SearchTool(String host, int port, Optional<String> auths) throws TTransportException {
    super(host, port, auths);
    LOGGER.debug("Running with host: {}, port: {}, auths: {}", host, port, auths);
    this.client = new SearchService.Client(protocol);
  }

  public SearchResult search(String query, SearchType type) throws TException, ServicesException {
    SearchQuery searchQuery = prepareQuery(query, type);
    LOGGER.info("Sending query: {}", searchQuery.toString());
    SearchResult results = new SearchResult();
    results = client.search(searchQuery);
    return results;
  }

  protected SearchQuery prepareQuery(String query, SearchType type) {
    SearchQuery searchQuery = new SearchQuery();
    searchQuery.setType(type);
    searchQuery.setRawQuery(query);
    auths.ifPresent(searchQuery::setAuths);
    Output output = getQuestions(query);
    searchQuery.setQuestions(output.items);
    searchQuery.setTerms(getTerms(output.query));

    // TODO: user id, query name, labels, communication id, tokens

    return searchQuery;
  }

  // copied from search UI JavaScript
  private static final String WORD_WITH_SPACE = "[^\\?\\\"]";
  private static final String WORD = "[^\\?\\\"\\s]";
  private static final Pattern PATTERN= Pattern.compile("\\?\\:(\\\")?(" + WORD + "+\\s*)+(\\?)?(\\\")?");

  protected static Output getQuestions(String query) {
    // ?:What is blue?
    // ?:"What is blue?"
    // ?:What is blue

    Matcher matcher = PATTERN.matcher(query);
    List<String> questions = new ArrayList<>();
    while (matcher.find()) {
      String group = matcher.group();
      query = query.replaceFirst(Pattern.quote(group), "");
      group = group.replaceAll("^\\?", "").replaceAll("[:\\\"]", "");
      questions.add(group);
    }
    query = query.trim();

    return new Output(query, questions);
  }

  protected static List<String> getTerms(String query) {
    // "queen elizabeth"
    // groceries expensive foods
    Pattern pattern = Pattern.compile("\\\"" + WORD_WITH_SPACE + "+\\\"|" + WORD + "+");
    Matcher matcher = pattern.matcher(query);
    List<String> terms = new ArrayList<>();
    while (matcher.find()) {
      String group = matcher.group();
      group = group.replaceAll("\"", "");
      terms.add(group);
    }

    return terms;
  }

  protected static class Output {
    public String query;
    public List<String> items;

    public Output(String query, List<String> items) {
      this.query = query;
      this.items = items;
    }
  }

  @Override
  public void close() {
    if (this.transport.isOpen())
      this.transport.close();
  }

  static class Opts {

    @Parameter(description = "Search query",
        required = true)
    List<String> queryList;

    @Parameter(names = { "--type", "-t" },
        description = "Type of search query (see help string)")
    String type = "sentences";

    @Parameter(help = true, names = { "--help", "-h" },
        description = "Print the help message and exit.")
    boolean help;
  }

  public static void main(String[] args) {
    Opts opts = new Opts();
    JCommander jc = new JCommander(opts);
    jc.setProgramName("./search.sh");
    try {
      jc.parse(args);
    } catch (ParameterException e) {
      jc.usage();
      System.out.println("Supported search types: ");
      for (SearchType st : SearchType.values())
        System.out.println(st.toString());

      System.exit(-1);
    }

    if (opts.help) {
      jc.usage();
      return;
    }

    String query = String.join(" ", opts.queryList);
    try (SearchTool tool = new SearchTool(new ConcreteServicesSearchConfig());) {
      SearchType st;
      // preserve old behavior
      if (opts.type.equals("comm"))
        st = SearchType.COMMUNICATIONS;
      else
        try {
          st = SearchType.valueOf(opts.type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ex) {
          System.out.println("Provided search type is not supported: " + opts.type);
          return;
        }
      SearchResult results = tool.search(query, st);
      if (results.getSearchResultItems() != null && results.getSearchResultItemsSize() > 0) {
        System.out.println("Score\tCommunicationId\tEntityUUID\tSentenceId\tTokens");
        for (SearchResultItem result : results.getSearchResultItems()) {
          System.out.print(result.getScore());
          System.out.print("\t");
          System.out.print(result.getCommunicationId());
          System.out.print("\t");
          System.out.print(result.getEntity().getUuid().getUuidString());
          System.out.print("\t");
          System.out.print(result.getSentenceId());
          System.out.print("\t");
          if (result.isSetTokens()) {
            TokenRefSequence tokens = result.getTokens();
            TextSpan span = tokens.getTextSpan();
            if (span != null) {
              System.out.print(span.getStart());
              System.out.print("-");
              System.out.print(span.getEnding());
            } else {
              System.out.print("no text span");
            }
          } else {
            System.out.print("null");
          }
          System.out.println();
        }
      } else
        System.out.println("Service returned null results or no results.");

    } catch (TException e) {
      LOGGER.error("Failed to get search result: {}", e.getMessage());
      System.exit(-1);
    }
  }
}
