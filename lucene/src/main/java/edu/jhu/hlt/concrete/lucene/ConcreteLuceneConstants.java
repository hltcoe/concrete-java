package edu.jhu.hlt.concrete.lucene;

import java.util.UUID;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;

import edu.jhu.hlt.concrete.miscommunication.NonEmptyString;

public class ConcreteLuceneConstants {
  // @deprecated Use COMM_UUID_FIELD instead
  public static final String UUID_FIELD = "uuid";

  public static final String COMM_ID_FIELD = "comm-id";
  public static final String COMM_UUID_FIELD = "comm-uuid";
  public static final String SENT_UUID_FIELD = "sent-uuid";
  public static final String TEXT_FIELD = "text";
  public static final String AUTHOR_ID_FIELD = "author-id";

  // @deprecated Use getCommunicationUUIDField instead
  public static final StoredField getUUIDField(UUID uuid) {
    return new StoredField(UUID_FIELD, uuid.toString());
  }

  public static final StoredField getCommunicationUUIDField(UUID uuid) {
    return new StoredField(COMM_UUID_FIELD, uuid.toString());
  }

  public static final StoredField getSentenceUUIDField(UUID uuid) {
    return new StoredField(SENT_UUID_FIELD, uuid.toString());
  }

  public static final StoredField getCommunicationIDField(NonEmptyString id) {
    return new StoredField(COMM_ID_FIELD, id.getContent());
  }

  public static final StoredField getCommunicationIDField(String id) {
    return new StoredField(COMM_ID_FIELD, id);
  }

  public static final FieldType getContentFieldType() {
    FieldType ft = new FieldType(TextField.TYPE_NOT_STORED);
    ft.setStoreTermVectors(true);
    ft.setStoreTermVectorOffsets(true);
    return ft;
  }
}
