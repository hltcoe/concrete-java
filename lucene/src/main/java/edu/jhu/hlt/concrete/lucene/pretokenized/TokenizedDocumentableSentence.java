package edu.jhu.hlt.concrete.lucene.pretokenized;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.lucene.ConcreteLuceneConstants;
import edu.jhu.hlt.concrete.lucene.LuceneDocumentable;

public class TokenizedDocumentableSentence implements LuceneDocumentable {
  private static final Logger LOGGER = LoggerFactory.getLogger(TokenizedDocumentableSentence.class);

  final private UUID commUUID;
  final private String commID;
  final private UUID sentUUID;
  private List<String> tokens = new ArrayList<String>();

  public TokenizedDocumentableSentence(Communication c, Sentence s) {
    commUUID = UUID.fromString(c.getUuid().getUuidString());
    commID = c.getId();
    sentUUID = UUID.fromString(s.getUuid().getUuidString());
    if (!s.isSetTokenization()) {
      LOGGER.warn("Communication with id " + c.getId() + " does not contain tokenization");
      return;
    }
    tokens = s.getTokenization().getTokenList().getTokenList()
                .stream()
                .map(e -> e.getText())
                .collect(Collectors.toList());
  }

  @Override
  public Document getDocument() {
    final Document d = new Document();
    d.add(ConcreteLuceneConstants.getCommunicationUUIDField(commUUID));
    d.add(ConcreteLuceneConstants.getCommunicationIDField(commID));
    d.add(ConcreteLuceneConstants.getSentenceUUIDField(sentUUID));

    for (String token : tokens) {
      d.add(new Field(ConcreteLuceneConstants.TEXT_FIELD,
          token, ConcreteLuceneConstants.getContentFieldType()));
    }

    return d;
  }

}
