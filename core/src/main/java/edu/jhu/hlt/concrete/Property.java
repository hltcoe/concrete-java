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
/**
 * Attached to Arguments to support situations where
 * a 'participant' has more than one 'property' (in BinarySRL terms),
 * whereas Arguments notionally only support one Role.
 */
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-01-04")
public class Property implements org.apache.thrift.TBase<Property, Property._Fields>, java.io.Serializable, Cloneable, Comparable<Property> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Property");

  private static final org.apache.thrift.protocol.TField VALUE_FIELD_DESC = new org.apache.thrift.protocol.TField("value", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField METADATA_FIELD_DESC = new org.apache.thrift.protocol.TField("metadata", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField POLARITY_FIELD_DESC = new org.apache.thrift.protocol.TField("polarity", org.apache.thrift.protocol.TType.DOUBLE, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new PropertyStandardSchemeFactory());
    schemes.put(TupleScheme.class, new PropertyTupleSchemeFactory());
  }

  private String value; // required
  private edu.jhu.hlt.concrete.AnnotationMetadata metadata; // required
  private double polarity; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * The required value of the property.
     */
    VALUE((short)1, "value"),
    /**
     * Metadata to support this particular property object.
     */
    METADATA((short)2, "metadata"),
    /**
     * This value is typically boolean, 0.0 or 1.0, but we use a
     * float in order to potentially capture cases where an annotator is
     * highly confident that the value is underspecified, via a value of
     * 0.5.
     */
    POLARITY((short)3, "polarity");

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
        case 1: // VALUE
          return VALUE;
        case 2: // METADATA
          return METADATA;
        case 3: // POLARITY
          return POLARITY;
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
  private static final int __POLARITY_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.POLARITY};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.VALUE, new org.apache.thrift.meta_data.FieldMetaData("value", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.METADATA, new org.apache.thrift.meta_data.FieldMetaData("metadata", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, edu.jhu.hlt.concrete.AnnotationMetadata.class)));
    tmpMap.put(_Fields.POLARITY, new org.apache.thrift.meta_data.FieldMetaData("polarity", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Property.class, metaDataMap);
  }

  public Property() {
  }

  public Property(
    String value,
    edu.jhu.hlt.concrete.AnnotationMetadata metadata)
  {
    this();
    this.value = value;
    this.metadata = metadata;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Property(Property other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetValue()) {
      this.value = other.value;
    }
    if (other.isSetMetadata()) {
      this.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata(other.metadata);
    }
    this.polarity = other.polarity;
  }

  public Property deepCopy() {
    return new Property(this);
  }

  @Override
  public void clear() {
    this.value = null;
    this.metadata = null;
    setPolarityIsSet(false);
    this.polarity = 0.0;
  }

  /**
   * The required value of the property.
   */
  public String getValue() {
    return this.value;
  }

  /**
   * The required value of the property.
   */
  public Property setValue(String value) {
    this.value = value;
    return this;
  }

  public void unsetValue() {
    this.value = null;
  }

  /** Returns true if field value is set (has been assigned a value) and false otherwise */
  public boolean isSetValue() {
    return this.value != null;
  }

  public void setValueIsSet(boolean value) {
    if (!value) {
      this.value = null;
    }
  }

  /**
   * Metadata to support this particular property object.
   */
  public edu.jhu.hlt.concrete.AnnotationMetadata getMetadata() {
    return this.metadata;
  }

  /**
   * Metadata to support this particular property object.
   */
  public Property setMetadata(edu.jhu.hlt.concrete.AnnotationMetadata metadata) {
    this.metadata = metadata;
    return this;
  }

  public void unsetMetadata() {
    this.metadata = null;
  }

  /** Returns true if field metadata is set (has been assigned a value) and false otherwise */
  public boolean isSetMetadata() {
    return this.metadata != null;
  }

  public void setMetadataIsSet(boolean value) {
    if (!value) {
      this.metadata = null;
    }
  }

  /**
   * This value is typically boolean, 0.0 or 1.0, but we use a
   * float in order to potentially capture cases where an annotator is
   * highly confident that the value is underspecified, via a value of
   * 0.5.
   */
  public double getPolarity() {
    return this.polarity;
  }

  /**
   * This value is typically boolean, 0.0 or 1.0, but we use a
   * float in order to potentially capture cases where an annotator is
   * highly confident that the value is underspecified, via a value of
   * 0.5.
   */
  public Property setPolarity(double polarity) {
    this.polarity = polarity;
    setPolarityIsSet(true);
    return this;
  }

  public void unsetPolarity() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __POLARITY_ISSET_ID);
  }

  /** Returns true if field polarity is set (has been assigned a value) and false otherwise */
  public boolean isSetPolarity() {
    return EncodingUtils.testBit(__isset_bitfield, __POLARITY_ISSET_ID);
  }

  public void setPolarityIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __POLARITY_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case VALUE:
      if (value == null) {
        unsetValue();
      } else {
        setValue((String)value);
      }
      break;

    case METADATA:
      if (value == null) {
        unsetMetadata();
      } else {
        setMetadata((edu.jhu.hlt.concrete.AnnotationMetadata)value);
      }
      break;

    case POLARITY:
      if (value == null) {
        unsetPolarity();
      } else {
        setPolarity((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case VALUE:
      return getValue();

    case METADATA:
      return getMetadata();

    case POLARITY:
      return getPolarity();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case VALUE:
      return isSetValue();
    case METADATA:
      return isSetMetadata();
    case POLARITY:
      return isSetPolarity();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Property)
      return this.equals((Property)that);
    return false;
  }

  public boolean equals(Property that) {
    if (that == null)
      return false;

    boolean this_present_value = true && this.isSetValue();
    boolean that_present_value = true && that.isSetValue();
    if (this_present_value || that_present_value) {
      if (!(this_present_value && that_present_value))
        return false;
      if (!this.value.equals(that.value))
        return false;
    }

    boolean this_present_metadata = true && this.isSetMetadata();
    boolean that_present_metadata = true && that.isSetMetadata();
    if (this_present_metadata || that_present_metadata) {
      if (!(this_present_metadata && that_present_metadata))
        return false;
      if (!this.metadata.equals(that.metadata))
        return false;
    }

    boolean this_present_polarity = true && this.isSetPolarity();
    boolean that_present_polarity = true && that.isSetPolarity();
    if (this_present_polarity || that_present_polarity) {
      if (!(this_present_polarity && that_present_polarity))
        return false;
      if (this.polarity != that.polarity)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_value = true && (isSetValue());
    list.add(present_value);
    if (present_value)
      list.add(value);

    boolean present_metadata = true && (isSetMetadata());
    list.add(present_metadata);
    if (present_metadata)
      list.add(metadata);

    boolean present_polarity = true && (isSetPolarity());
    list.add(present_polarity);
    if (present_polarity)
      list.add(polarity);

    return list.hashCode();
  }

  @Override
  public int compareTo(Property other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetValue()).compareTo(other.isSetValue());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetValue()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.value, other.value);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMetadata()).compareTo(other.isSetMetadata());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMetadata()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.metadata, other.metadata);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPolarity()).compareTo(other.isSetPolarity());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPolarity()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.polarity, other.polarity);
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
    StringBuilder sb = new StringBuilder("Property(");
    boolean first = true;

    sb.append("value:");
    if (this.value == null) {
      sb.append("null");
    } else {
      sb.append(this.value);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("metadata:");
    if (this.metadata == null) {
      sb.append("null");
    } else {
      sb.append(this.metadata);
    }
    first = false;
    if (isSetPolarity()) {
      if (!first) sb.append(", ");
      sb.append("polarity:");
      sb.append(this.polarity);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (value == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'value' was not present! Struct: " + toString());
    }
    if (metadata == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'metadata' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (metadata != null) {
      metadata.validate();
    }
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class PropertyStandardSchemeFactory implements SchemeFactory {
    public PropertyStandardScheme getScheme() {
      return new PropertyStandardScheme();
    }
  }

  private static class PropertyStandardScheme extends StandardScheme<Property> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Property struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // VALUE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.value = iprot.readString();
              struct.setValueIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // METADATA
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata();
              struct.metadata.read(iprot);
              struct.setMetadataIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // POLARITY
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.polarity = iprot.readDouble();
              struct.setPolarityIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Property struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.value != null) {
        oprot.writeFieldBegin(VALUE_FIELD_DESC);
        oprot.writeString(struct.value);
        oprot.writeFieldEnd();
      }
      if (struct.metadata != null) {
        oprot.writeFieldBegin(METADATA_FIELD_DESC);
        struct.metadata.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.isSetPolarity()) {
        oprot.writeFieldBegin(POLARITY_FIELD_DESC);
        oprot.writeDouble(struct.polarity);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class PropertyTupleSchemeFactory implements SchemeFactory {
    public PropertyTupleScheme getScheme() {
      return new PropertyTupleScheme();
    }
  }

  private static class PropertyTupleScheme extends TupleScheme<Property> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Property struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.value);
      struct.metadata.write(oprot);
      BitSet optionals = new BitSet();
      if (struct.isSetPolarity()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetPolarity()) {
        oprot.writeDouble(struct.polarity);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Property struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.value = iprot.readString();
      struct.setValueIsSet(true);
      struct.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata();
      struct.metadata.read(iprot);
      struct.setMetadataIsSet(true);
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.polarity = iprot.readDouble();
        struct.setPolarityIsSet(true);
      }
    }
  }

}

