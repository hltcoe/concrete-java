package edu.jhu.hlt.concrete.lucene;

import java.util.UUID;

import org.apache.lucene.document.StoredField;

import edu.jhu.hlt.concrete.miscommunication.NonEmptyString;

public class ConcreteLuceneConstants {
  public static final String UUID_FIELD = "uuid";

  public static final String COMM_ID_FIELD = "comm-id";
  public static final String COMM_UUID_FIELD = "comm-uuid";

  public static final StoredField getUUIDField(UUID uuid) {
    return new StoredField(UUID_FIELD, uuid.toString());
  }

  public static final StoredField getCommunicationUUIDField(UUID uuid) {
    return new StoredField(COMM_UUID_FIELD, uuid.toString());
  }

  public static final StoredField getCommunicationIDField(NonEmptyString id) {
    return new StoredField(COMM_ID_FIELD, id.getContent());
  }
}
