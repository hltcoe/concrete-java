package edu.jhu.hlt.concrete.lucene;

import java.util.Optional;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.miscommunication.MiscSentence;
import edu.jhu.hlt.concrete.miscommunication.MiscTextSpan;

/**
 * Implementation of {@link LuceneDocumentable} that represents
 * a Concrete {@link Sentence} object but does not use tokenization.
 *
 * @see MiscSentence
 */
@FreeBuilder
public abstract class DocumentableSentence implements MiscSentence, LuceneDocumentable {

  public abstract MiscSentence getSentence();
  public abstract Optional<Long> getAuthorId();

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.lucene.LuceneDocumentable#getDocument()
   */
  @Override
  public final Document getDocument() {
    final Document d = new Document();
    d.add(ConcreteLuceneConstants.getCommunicationUUIDField(this.getUUID()));
    d.add(ConcreteLuceneConstants.getCommunicationIDField(this.getCommunicationID()));
    d.add(ConcreteLuceneConstants.getSentenceUUIDField(this.getSentence().getUUID()));

    this.getAuthorId().ifPresent(aid -> {
      d.add(new StringField(ConcreteLuceneConstants.AUTHOR_ID_FIELD, aid.toString(), Store.NO));
    });

    this.getTextSpan()
        .map(MiscTextSpan::getText)
        .ifPresent(txt -> d.add(new Field(ConcreteLuceneConstants.TEXT_FIELD,
            txt.getContent(), ConcreteLuceneConstants.getContentFieldType())));
    return d;
  }

  public static DocumentableSentence create(MiscSentence mc) {
    return create(mc, Optional.empty());
  }

  public static DocumentableSentence create(MiscSentence mc, Optional<Long> authorId) {
    return new Builder()
        .setSentence(mc)
        .setCommunicationID(mc.getCommunicationID())
        .setUUID(mc.getUUID())
        .setTokenization(mc.getTokenization())
        .setTextSpan(mc.getTextSpan())
        .setAuthorId(authorId)
        .build();
  }

  public static class Builder extends DocumentableSentence_Builder {
    public Builder() {

    }
  }
}
