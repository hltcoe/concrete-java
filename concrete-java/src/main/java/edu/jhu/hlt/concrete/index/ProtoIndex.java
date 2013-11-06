/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.util.ByteUtil;
import edu.jhu.hlt.concrete.util.IdUtil;

// We might consider adding other callbacks -- e.g., all that
// IndexedCommunication really cares about is when a new edge is
// added.  Similarly, most things probably mostly just care about new
// things getting added, since everything's monotonic..  Though it's a
// little tricky, since (a) you only want to hear about changes under
// your 'local root'; and (b) you may need to know the ancestry of the
// modified object.. hm.

/**
 * A wrapper around a "root" protobuf object that maintains a UUID-based index of all subobjects that define a 'uuid' field.
 * 
 * In addition, the methods 'addField()' and 'setField()' may be used to make monotonic changes to the protobuf object (or any contained object with a uuid).
 * When such changes are made, the wrapped object is replaced (since protobuf objects are immutable), and all indices are updated appropraitely. In addition,
 * all changes are tracked, allowing us to write a specific set of changes as the stage output for a corpus or knowledge graph.
 * 
 * A single ProtoIndex may be shared by multiple IndexedXYZ objects. For example, an IndexedCommunication might use a ProtoIndex to index the communication, and
 * then use the same index when building IndexedTokenization objects. This ensures that all changes get appropriately propagated all the way up to the "root"
 * object.
 */
public final class ProtoIndex {
  private Message root;

  // ======================================================================
  // Private Variables
  // ======================================================================

  /** Index of all messages that have a 'uuid' field. */
  private final Map<Concrete.UUID, Message> uuidIndex;

  /**
   * Pointers from messages to their parents. We only bother to add messages to this if they have a UUID, or if any of their descendents have a UUID. The
   * primary purpose of this parent index is to allow us to make modifications.
   * 
   * (Note: we could make the value of this map contain more information -- e.g., the field that holds the message and the index for repeated fields. This might
   * make modifications faster, but would make indexing slower and memory usage larger.)
   */
  private final IdentityHashMap<Message, Message> parentIndex;

  /**
   * Pointers to callback functions that should be called whenever we replace a given protobuf message with a new message. This callback is called before we do
   * the actual replacement.
   */
  private final IdentityHashMap<Message, ReplaceProtoCallback> replaceProtoCallbacks;

  /**
   * A list of modifications that have been made but that have not yet been saved to the Corpus. This variable maps from UUIDs of modified objects to serialized
   * protobuf messages.
   */
  private Map<ModificationTarget, byte[]> unsavedModifications = 
          new HashMap<>();

  /** Keys of this map are UUIDs or EdgeIds: */
  private Map<Message, SoftReference<IndexedProto<?>>> indexedProtos;

  // ======================================================================
  // Constructor
  // ======================================================================

  public ProtoIndex(Message root) throws ConcreteException {
    this.root = root;
    uuidIndex = new HashMap<Concrete.UUID, Message>();
    parentIndex = new IdentityHashMap<Message, Message>();
    replaceProtoCallbacks = new IdentityHashMap<Message, ReplaceProtoCallback>();
    parentIndex.put(root, null);
    buildIndex(root);
    indexedProtos = new HashMap<Message, SoftReference<IndexedProto<?>>>();
    /*
     * for (Map.Entry<Message,Message> p: parentIndex.entrySet()) { System.err.println(protoDebugString(p.getKey())+"'s parent is "+
     * protoDebugString(p.getValue())); }
     */
  }

  public Message getRoot() {
    return root;
  }

  // ======================================================================
  // Callback Hooks
  // ======================================================================

  /**
   * Interface used to handle a notification that is generated whenever the protobuf object with a specific UUID gets replaced with a new protobuf object. (The
   * UUID is specified when registering the ReplaceProtoCallback.)
   */
  interface ReplaceProtoCallback {
    public void replace(Message oldMsg, Message newMsg) throws ConcreteException;
  }

  public void registerCallback(Message message, ReplaceProtoCallback callback) throws ConcreteException {
    replaceProtoCallbacks.put(message, callback);
  }

  // ======================================================================
  // Modifications
  // ======================================================================

  /**
   * Append a new value to a specified repeated field in given target message. The target message must be contained in this indexed proto, and must have a UUID.
   */
  public void addField(Message target, FieldDescriptor field, Message fieldValue) throws ConcreteException {
    // Input validity check:
    if (!parentIndex.containsKey(target))
      throw reportBadFieldTarget(target, "addField");
    // Make the change.
    Message newTarget = target.toBuilder().addRepeatedField(field, fieldValue).build();
    replaceAndUpdate(target, newTarget, field, fieldValue);
    // Final sanity check
    Object newFieldValue = newTarget.getRepeatedField(field, newTarget.getRepeatedFieldCount(field) - 1);
    if (newFieldValue != fieldValue)
      throw new ConcreteException("Identity changed in unexpected way");
  }

  /**
   * Set the value of a specified optional field in given target message. The field must not already have a value. The target message must be contained in this
   * indexed proto, and must have a UUID.
   */
  public void setField(Message target, FieldDescriptor field, Message fieldValue) throws ConcreteException {
    // Input validity check:
    if (!parentIndex.containsKey(target))
      throw reportBadFieldTarget(target, "setField");
    if (target.hasField(field))
      throw reportDuplicateField(target, field);
    // Make the change.
    Message newTarget = target.toBuilder().setField(field, fieldValue).build();
    replaceAndUpdate(target, newTarget, field, fieldValue);
    // Final sanity check
    if (newTarget.getField(field) != fieldValue)
      throw new ConcreteException("Identity changed in unexpected way");
  }

  /**
   * Get the set of unsaved modifications, or null if no modifications have been made. This should only be called when actually saving the modifications.
   */
  public Map<ModificationTarget, byte[]> getUnsavedModifications() {
    return unsavedModifications;
  }

  /**
   * Clear the list of unsaved modifications. This should be called once the modifications returned by getUnsavedModifications() have been saved.
   */
  public void clearUnsavedModifications() {
    unsavedModifications = new HashMap<>();
  }

  // ======================================================================
  // Generic Index Lookup
  // ======================================================================

  /**
   * Return the protobuf object within this indexed proto that has the specified UUID, or null if no such object is found.
   */
  public Message lookup(Concrete.UUID uuid) throws ConcreteException {
    return uuidIndex.get(uuid);
  }

  // ======================================================================
  // Indexed objects
  // ======================================================================
  // We keep track of all IndexedProto objects that have been
  // created using this ProtoIndex. They are usually indexed by
  // UUID, but can be indexed by other keys as well (eg EdgeId).

  /**
   * Return the IndexedProto for the given UUID if it has already been created; or null otherwise.
   */
  @SuppressWarnings("unchecked")
  public <T extends IndexedProto<?>> T getIndexedProto(Message key) throws ConcreteException {
    SoftReference<IndexedProto<?>> ref = indexedProtos.get(key);
    return (ref == null) ? null : (T) ref.get();
  }

  protected <T extends IndexedProto<?>> void registerIndexedProto(Message key, T indexedProto) throws ConcreteException {
    indexedProtos.put(key, new SoftReference<IndexedProto<?>>(indexedProto));
  }

  // ======================================================================
  // Private Helper Methods
  // ======================================================================

  private boolean buildIndex(Message msg) throws ConcreteException {
    // We will add a message to the parent index if: (1) it
    // contains a UUID, (2) any descendent contains a UUID, or (3)
    // it is one of a special list of edge-related types that
    // always get added even though they don't have UUIDs.
    boolean addToParentIndex = false;
    // Index each field in this message.
    for (Map.Entry<FieldDescriptor, Object> entry : msg.getAllFields().entrySet()) {
      FieldDescriptor field = entry.getKey();
      if (field.getName().equals("uuid")) {
        Concrete.UUID uuid = (Concrete.UUID) (entry.getValue());
        if (uuidIndex.put(uuid, msg) != null)
          throw new ConcreteException("Duplicate UUID detected: " + uuid);
        addToParentIndex = true;
      } else if (field.getType() == FieldDescriptor.Type.MESSAGE) {
        if (field.isRepeated()) {
          @SuppressWarnings("unchecked")
          List<Message> children = (List<Message>) entry.getValue();
          for (Message child : children) {
            if (buildIndex(child)) {
              parentIndex.put(child, msg);
              addToParentIndex = true;
            }
          }
        } else {
          Message child = (Message) (entry.getValue());
          if (buildIndex(child)) {
            parentIndex.put(child, msg);
            addToParentIndex = true;
          }
        }
      }
    }
    return addToParentIndex;
  }

  /** Helper method for addField() and setField(). */
  private void replaceAndUpdate(Message oldMsg, Message newMsg, FieldDescriptor field, Message fieldValue) throws ConcreteException {
    // Normally, the oldMsg will always have a UUID. But in a few
    // special cases (notably edges, where the root and its
    // attribute children don't have uuids), we won't. In that
    // case, we're still fine as long as oldMsg is in the parent index.
    if (!parentIndex.containsKey(oldMsg))
      throw new ConcreteException("oldMsg is not in the parent index -- invalid target for modification");
    // Swap in the new message.
    replaceMessage(oldMsg, newMsg);
    // Add the new field value to our index.
    if (buildIndex(fieldValue))
      parentIndex.put(fieldValue, newMsg);
    // Record the change for output to a persistent datastore.
    recordModification(newMsg, field, fieldValue);
  }

  /**
   * Helper method for addField() and setField(). Walks up the chain of parents, replacing protobuf messages as necessary. Also updates any affected indices.
   */
  private void replaceMessage(Message oldMsg, Message newMsg) throws ConcreteException {
    // Replace the old message with the new message in the uuid map.
    Concrete.UUID uuid = IdUtil.getUUIDOrNull(oldMsg);
    if (uuid != null)
      uuidIndex.put(uuid, newMsg);

    // Update parent pointers of any children of the new message.
    updateParentIndex(newMsg);

    // If there's a callback registered for this protobuf object,
    // then call it.
    ReplaceProtoCallback callback = replaceProtoCallbacks.remove(oldMsg);
    if (callback != null) {
      replaceProtoCallbacks.put(newMsg, callback);
      callback.replace(oldMsg, newMsg);
    }

    if (oldMsg == root) {
      parentIndex.remove(oldMsg);
      parentIndex.put(newMsg, null);
      root = newMsg;
    } else {
      // Recursive case: find the parent of oldMsg, and replace
      // that parent with a new message where oldMsg is replaced
      // by newMsg.
      Message parent = parentIndex.remove(oldMsg);
      assert (parent != null);
      for (Map.Entry<FieldDescriptor, Object> entry : parent.getAllFields().entrySet()) {
        FieldDescriptor field = entry.getKey();
        if (field.getType() == FieldDescriptor.Type.MESSAGE) {
          if (field.isRepeated()) {
            @SuppressWarnings("unchecked")
            List<Message> children = (List<Message>) entry.getValue();
            for (int i = 0; i < children.size(); i++) {
              Message child = children.get(i);
              if (child == oldMsg) {
                Message newParent = parent.toBuilder().setRepeatedField(field, i, newMsg).build();
                replaceMessage(parent, newParent);
                return;
              }
            }
          } else {
            Message child = (Message) entry.getValue();
            if (child == oldMsg) {
              Message newParent = parent.toBuilder().setField(field, newMsg).build();
              replaceMessage(parent, newParent);
              return;
            }
          }
        }
      }
      // If we get here, then parentIndex is corrupt/incorrect.
      throw new ConcreteException("oldMsg not found in parentIndex.get(oldMsg)!");
    }
  }

  /**
   * Helper for replaceMessage() -- Given a message, modify all of its direct children to point to that message in the parent map. This is necessary whenever we
   * replace a protobuf object with a new one, since the old parent pointers all point to the old one.
   */
  private void updateParentIndex(Message parent) throws ConcreteException {
    for (Map.Entry<FieldDescriptor, Object> entry : parent.getAllFields().entrySet()) {
      FieldDescriptor field = entry.getKey();
      if (field.getType() == FieldDescriptor.Type.MESSAGE) {
        if (field.isRepeated()) {
          @SuppressWarnings("unchecked")
          List<Message> children = (List<Message>) entry.getValue();
          for (Message child : children)
            parentIndex.put(child, parent);
        } else {
          Message child = (Message) (entry.getValue());
          parentIndex.put(child, parent);
        }
      }
    }
  }

  //private final static ModificationTarget ZERO_UUID_MODIFICATION_TARGET = new ModificationTarget(Concrete.UUID.newBuilder().setHigh(0).setLow(0).build());

  private void recordModification(Message target, FieldDescriptor field, Message fieldValue) throws ConcreteException {
    // if ((target instanceof Concrete.UUID) || (target instanceof
    // Concrete.EdgeId))
    // throw new ConcreteException("You should not be editing UUIDS!");
    // System.err.println("Recording modification to "+protoDebugString(target));
    // System.err.println("  "+field.getFullName()+"="+protoDebugString(fieldValue));

    Concrete.UUID uuid = IdUtil.getUUIDOrNull(target);
    if (uuid != null) {
      ModificationTarget mTarget = new ModificationTarget(uuid);
      recordModification(mTarget, field, fieldValue);
    } else {
      // Build a new message with the same type as target, that
      // can be used to merge in the field value.
      final Message newValue;
      if (field.isRepeated())
        newValue = target.newBuilderForType().addRepeatedField(field, fieldValue).buildPartial();
      else
        newValue = target.newBuilderForType().setField(field, fieldValue).buildPartial();
      // Find the field in the target's parent that contains the
      // parent. We only need to consider non-repeated fields,
      // since you can't "merge into" a repeated field.
      Message parent = parentIndex.get(target);
      for (Map.Entry<FieldDescriptor, Object> entry : parent.getAllFields().entrySet()) {
        FieldDescriptor parentField = entry.getKey();
        if (parentField.getType() == FieldDescriptor.Type.MESSAGE) {
          if (!parentField.isRepeated()) {
            Message child = (Message) entry.getValue();
            if (child == target) {
              recordModification(parent, parentField, newValue);
              return;
            }
          }
        }
      }
      throw new ConcreteException("Invalid merge target (not reachable via non-" + "repeated fields from a message with a UUID");
    }
  }

  /** A target for a modification. Can be a UUID or a normalized EdgeId. */
  public static class ModificationTarget {
    public final Concrete.UUID uuid;

    public ModificationTarget(Concrete.UUID uuid) {
      this.uuid = uuid;
    }

    public ModificationTarget(byte[] bytes) {
      this.uuid = ByteUtil.toUUID(bytes);
    }

    public byte[] toBytes() {
      return ByteUtil.fromUUID(this.uuid);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ModificationTarget))
        return false;
      else
        return uuid.equals(((ModificationTarget) o).uuid);

    }

    @Override
    public int hashCode() {
      return uuid.hashCode();
    }

    @Override
    public String toString() {
      return IdUtil.uuidToString(uuid);
    }
  }

  private void recordModification(ModificationTarget target, FieldDescriptor field, Message fieldValue) throws ConcreteException {
    final int fieldNum = field.getNumber();
    final int size = CodedOutputStream.computeMessageSize(fieldNum, fieldValue);
    // Allocate space for the modification. If we already have
    // one or more modifications for this UUID, then copy the old
    // modification(s) into this new array.
    byte[] buf = null;
    CodedOutputStream out = null;
    byte[] oldBuf = unsavedModifications.get(target);
    if (oldBuf != null) {
      buf = new byte[size + oldBuf.length];
      System.arraycopy(oldBuf, 0, buf, 0, oldBuf.length);
      out = CodedOutputStream.newInstance(buf, oldBuf.length, buf.length);
    } else {
      buf = new byte[size];
      out = CodedOutputStream.newInstance(buf);
    }
    // Write the new modification.
    try {
      out.writeMessage(fieldNum, fieldValue);
    } catch (java.io.IOException e) {
      throw new ConcreteException(e);
    }
    // Save it to unsavedModifications.
    unsavedModifications.put(target, buf);
  }

  /** Helper message for generating friendlier error messages */
  private String protoDebugString(Message m) {
    if (m == null)
      return "null";
    String protoMessageType = m.getDescriptorForType().getFullName();
    Concrete.UUID uuid = IdUtil.getUUIDOrNull(m);
    int identityHash = System.identityHashCode(m);
    if (uuid != null)
      return protoMessageType + "(uuid=" + IdUtil.uuidToString(uuid) + ")@" + identityHash;
    else
      return protoMessageType + "@" + identityHash;
  }

  private ConcreteException reportBadFieldTarget(Message m, String methodName) throws ConcreteException {
    Concrete.UUID uuid = IdUtil.getUUIDOrNull(m);
    if (uuid == null)
      return new ConcreteException(protoDebugString(m) + " is not a valid target " + "for " + methodName + " because it does not " + "have a UUID.");
    else {
      /*
       * System.err.println(root); for (Map.Entry<Message,Message> p: parentIndex.entrySet()) { System.err.println(protoDebugString(p.getKey())+"'s parent is "+
       * protoDebugString(p.getValue())); }
       */
      return new ConcreteException(protoDebugString(m) + " not found inside " + protoDebugString(root));
    }
  }

  private ConcreteException reportDuplicateField(Message target, FieldDescriptor field) throws ConcreteException {
    throw new ConcreteException(protoDebugString(target) + " already has a " + field.getName() + " value");
  }
}
