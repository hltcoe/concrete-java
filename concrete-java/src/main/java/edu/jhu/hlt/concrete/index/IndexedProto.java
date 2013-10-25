/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.util.IdUtil;

public abstract class IndexedProto<ProtoObj extends Message> {
  // ======================================================================
  // Private Variables
  // ======================================================================
  /** The index that backs this IndexedProto. */
  private ProtoIndex index;

  /** The indexed protobuf object itself */
  protected ProtoObj protoObj;

  // ======================================================================
  // Constructor
  // ======================================================================

  public IndexedProto(ProtoObj protoObj, ProtoIndex index) throws ConcreteException {
    this.index = index;
    this.protoObj = protoObj;
    registerCallback();
    registerIndexedProto();
  }

  protected void registerIndexedProto() throws ConcreteException {
    index.registerIndexedProto(this.getUUID(), this);
  }

  protected void registerCallback() throws ConcreteException {
    index.registerCallback(protoObj, new ProtoIndex.ReplaceProtoCallback() {
      @Override
      public void replace(Message oldMsg, Message newMsg) throws ConcreteException {
        assert (oldMsg == protoObj);
        @SuppressWarnings("unchecked")
        ProtoObj newProtoObj = (ProtoObj) newMsg;
        protoObj = newProtoObj;
        updateIndices();
      }
    });
  }

  protected void updateIndices() throws ConcreteException {
  }

  // ======================================================================
  // Modification Methods
  // ======================================================================

  /**
   * Append a new value to a specified repeated field in given target message. The target message must be contained in this indexed protobuf object, and must
   * have a UUID.
   */
  public void addField(Message target, FieldDescriptor field, Message fieldValue) throws ConcreteException {
    index.addField(target, field, fieldValue);
  }

  /**
   * Set the value of a specified optional field in given target message. The field must not already have a value. The target message must be contained in this
   * indexed protobuf object, and must have a UUID.
   */
  public void setField(Message target, FieldDescriptor field, Message fieldValue) throws ConcreteException {
    index.setField(target, field, fieldValue);
  }

  /**
   * Append a new value to a specified repeated field in this indexed protobuf object.
   */
  public void addField(FieldDescriptor field, Message fieldValue) throws ConcreteException {
    addField(protoObj, field, fieldValue);
  }

  /**
   * Set the value of a specified optional field in this indexed protobuf object.
   */
  public void setField(FieldDescriptor field, Message fieldValue) throws ConcreteException {
    setField(protoObj, field, fieldValue);
  }

  // ======================================================================
  // Accessors
  // ======================================================================

  /**
   * Return the protobuf object within this communication that has the specified UUID, or null if no such object is found.
   */
  public Message lookup(Concrete.UUID uuid) throws ConcreteException {
    return index.lookup(uuid);
  }

  /**
   * Return the protobuf object wrapped by this indexed protobuf object.
   */
  public ProtoObj getProto() {
    return protoObj;
  }

  /** Return the index that backs this indexed protobuf object. */
  public ProtoIndex getIndex() {
    return index;
  }

  public Concrete.UUID getUUID() throws ConcreteException {
    return IdUtil.getUUID(protoObj);
  }

  @Override
  public String toString() {
    return (protoObj.getDescriptorForType().getFullName() + "\n" + protoObj.toString());
  }

}
