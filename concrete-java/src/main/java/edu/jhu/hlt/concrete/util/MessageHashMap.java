package edu.jhu.hlt.concrete.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import edu.jhu.hlt.concrete.Concrete.UUID;

/**
 * @author John Sullivan
 */
public class MessageHashMap extends HashMap<UUID, Message> {

  private final String UUIDFIELD = "uuid";

  public MessageHashMap(Iterable<? extends Message> messages) {
    super();
    for (Message message : messages) {
      for (Entry<UUID, Message> kvPair : getMessageUUIDs(message)) {
        this.put(kvPair.getKey(), kvPair.getValue());
      }
    }
  }

  public void add(Message message) {
    for (Entry<UUID, Message> kvPair : getMessageUUIDs(message)) {
      this.put(kvPair.getKey(), kvPair.getValue());
    }
  }

  public void addAll(Iterable<? extends Message> messages) {
    for (Message message : messages) {
      this.add(message);
    }
  }

  public <M extends Message> M get(UUID key, Class<M> messageClass) {
    return messageClass.cast(this.get(key));
  }

  private List<Entry<UUID, Message>> getMessageUUIDs(Message message) {
    List<Entry<UUID, Message>> pairs = new ArrayList<Entry<UUID, Message>>();
    Descriptors.FieldDescriptor fieldDesc = message.getDescriptorForType().findFieldByName(UUIDFIELD);
    if (fieldDesc != null && fieldDesc.getMessageType() == UUID.getDescriptor()) { // if it has a uuid field of the appropriate type
      pairs.add(new SimpleImmutableEntry<UUID, Message>((UUID) message.getField(fieldDesc), message)); // add it's message to the list
    }
    Map<Descriptors.FieldDescriptor, Object> fieldMap = message.getAllFields();
    Iterator<Descriptors.FieldDescriptor> fields = fieldMap.keySet().iterator();
    while (fields.hasNext()) { // using while instead of foreach because fieldDesc has already been declared
      fieldDesc = fields.next();
      if (fieldDesc.getJavaType() != null && fieldDesc.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) { // if a child field is a message,
                                                                                                                        // recurse through to get it's uuid
        if (fieldDesc.isRepeated()) {
          List<Message> childMessages = (List<Message>) fieldMap.get(fieldDesc);
          for (Message childMessage : childMessages) {
            pairs.addAll(getMessageUUIDs(childMessage));
          }
        } else {
          Message childMessage = (Message) fieldMap.get(fieldDesc);
          pairs.addAll(getMessageUUIDs(childMessage));
        }
      }
    }
    return pairs;
  }

}
