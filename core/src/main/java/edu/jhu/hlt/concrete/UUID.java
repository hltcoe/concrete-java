/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package edu.jhu.hlt.concrete;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-01-13")
public class UUID implements org.apache.thrift.TBase<UUID, UUID._Fields>, java.io.Serializable, Cloneable, Comparable<UUID> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("UUID");

  private static final org.apache.thrift.protocol.TField UUID_STRING_FIELD_DESC = new org.apache.thrift.protocol.TField("uuidString", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new UUIDStandardSchemeFactory());
    schemes.put(TupleScheme.class, new UUIDTupleSchemeFactory());
  }

  private String uuidString; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * A string representation of a UUID, in the format of:
     * <pre>
     * 550e8400-e29b-41d4-a716-446655440000
     * </pre>
     */
    UUID_STRING((short)1, "uuidString");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // UUID_STRING
          return UUID_STRING;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.UUID_STRING, new org.apache.thrift.meta_data.FieldMetaData("uuidString", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(UUID.class, metaDataMap);
  }

  public UUID() {
  }

  public UUID(
    String uuidString)
  {
    this();
    this.uuidString = uuidString;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public UUID(UUID other) {
    if (other.isSetUuidString()) {
      this.uuidString = other.uuidString;
    }
  }

  public UUID deepCopy() {
    return new UUID(this);
  }

  @Override
  public void clear() {
    this.uuidString = null;
  }

  /**
   * A string representation of a UUID, in the format of:
   * <pre>
   * 550e8400-e29b-41d4-a716-446655440000
   * </pre>
   */
  public String getUuidString() {
    return this.uuidString;
  }

  /**
   * A string representation of a UUID, in the format of:
   * <pre>
   * 550e8400-e29b-41d4-a716-446655440000
   * </pre>
   */
  public UUID setUuidString(String uuidString) {
    this.uuidString = uuidString;
    return this;
  }

  public void unsetUuidString() {
    this.uuidString = null;
  }

  /** Returns true if field uuidString is set (has been assigned a value) and false otherwise */
  public boolean isSetUuidString() {
    return this.uuidString != null;
  }

  public void setUuidStringIsSet(boolean value) {
    if (!value) {
      this.uuidString = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case UUID_STRING:
      if (value == null) {
        unsetUuidString();
      } else {
        setUuidString((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case UUID_STRING:
      return getUuidString();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case UUID_STRING:
      return isSetUuidString();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof UUID)
      return this.equals((UUID)that);
    return false;
  }

  public boolean equals(UUID that) {
    if (that == null)
      return false;

    boolean this_present_uuidString = true && this.isSetUuidString();
    boolean that_present_uuidString = true && that.isSetUuidString();
    if (this_present_uuidString || that_present_uuidString) {
      if (!(this_present_uuidString && that_present_uuidString))
        return false;
      if (!this.uuidString.equals(that.uuidString))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_uuidString = true && (isSetUuidString());
    list.add(present_uuidString);
    if (present_uuidString)
      list.add(uuidString);

    return list.hashCode();
  }

  @Override
  public int compareTo(UUID other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetUuidString()).compareTo(other.isSetUuidString());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUuidString()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.uuidString, other.uuidString);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("UUID(");
    boolean first = true;

    sb.append("uuidString:");
    if (this.uuidString == null) {
      sb.append("null");
    } else {
      sb.append(this.uuidString);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (uuidString == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'uuidString' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class UUIDStandardSchemeFactory implements SchemeFactory {
    public UUIDStandardScheme getScheme() {
      return new UUIDStandardScheme();
    }
  }

  private static class UUIDStandardScheme extends StandardScheme<UUID> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, UUID struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // UUID_STRING
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.uuidString = iprot.readString();
              struct.setUuidStringIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, UUID struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.uuidString != null) {
        oprot.writeFieldBegin(UUID_STRING_FIELD_DESC);
        oprot.writeString(struct.uuidString);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class UUIDTupleSchemeFactory implements SchemeFactory {
    public UUIDTupleScheme getScheme() {
      return new UUIDTupleScheme();
    }
  }

  private static class UUIDTupleScheme extends TupleScheme<UUID> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, UUID struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.uuidString);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, UUID struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.uuidString = iprot.readString();
      struct.setUuidStringIsSet(true);
    }
  }

}
