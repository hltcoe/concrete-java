package edu.jhu.hlt.concrete.lucene;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

public interface LuceneCommunicationSearcher extends AutoCloseable {
  /**
   * Search for documents given a query
   * @param query Lucene query string
   * @param maxDocs Maximum number of documents to return
   * @return List of Document objects
   * @throws ParseException on parsing problem with the query string
   * @throws IOException on accessing the lucene index
   */
  public List<Document> searchDocuments(String query, int maxDocs) throws ParseException, IOException;

  /**
   * Search for documents given a query and limit the results to a particular author
   * @param query Lucene query string
   * @param authorId Author identifier
   * @param maxDocs Maximum number of documents to return
   * @return List of Document objects
   * @throws ParseException
   * @throws IOException
   */
  public List<Document> searchDocuments(String query, long authorId, int maxDocs) throws ParseException, IOException;

  /**
   * Close the index.
   */
  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws IOException;
}
