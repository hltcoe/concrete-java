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
 * A syntactic edge between two tokens in a tokenized sentence.
 */
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-01-04")
public class Dependency implements org.apache.thrift.TBase<Dependency, Dependency._Fields>, java.io.Serializable, Cloneable, Comparable<Dependency> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Dependency");

  private static final org.apache.thrift.protocol.TField GOV_FIELD_DESC = new org.apache.thrift.protocol.TField("gov", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField DEP_FIELD_DESC = new org.apache.thrift.protocol.TField("dep", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField EDGE_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("edgeType", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new DependencyStandardSchemeFactory());
    schemes.put(TupleScheme.class, new DependencyTupleSchemeFactory());
  }

  private int gov; // optional
  private int dep; // required
  private String edgeType; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * The governor or the head token. 0 indexed.
     */
    GOV((short)1, "gov"),
    /**
     * The dependent token. 0 indexed.
     */
    DEP((short)2, "dep"),
    /**
     * The relation that holds between gov and dep.
     */
    EDGE_TYPE((short)3, "edgeType");

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
        case 1: // GOV
          return GOV;
        case 2: // DEP
          return DEP;
        case 3: // EDGE_TYPE
          return EDGE_TYPE;
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
  private static final int __GOV_ISSET_ID = 0;
  private static final int __DEP_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.GOV,_Fields.EDGE_TYPE};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.GOV, new org.apache.thrift.meta_data.FieldMetaData("gov", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.DEP, new org.apache.thrift.meta_data.FieldMetaData("dep", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.EDGE_TYPE, new org.apache.thrift.meta_data.FieldMetaData("edgeType", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Dependency.class, metaDataMap);
  }

  public Dependency() {
    this.gov = -1;

  }

  public Dependency(
    int dep)
  {
    this();
    this.dep = dep;
    setDepIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Dependency(Dependency other) {
    __isset_bitfield = other.__isset_bitfield;
    this.gov = other.gov;
    this.dep = other.dep;
    if (other.isSetEdgeType()) {
      this.edgeType = other.edgeType;
    }
  }

  public Dependency deepCopy() {
    return new Dependency(this);
  }

  @Override
  public void clear() {
    this.gov = -1;

    setDepIsSet(false);
    this.dep = 0;
    this.edgeType = null;
  }

  /**
   * The governor or the head token. 0 indexed.
   */
  public int getGov() {
    return this.gov;
  }

  /**
   * The governor or the head token. 0 indexed.
   */
  public Dependency setGov(int gov) {
    this.gov = gov;
    setGovIsSet(true);
    return this;
  }

  public void unsetGov() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __GOV_ISSET_ID);
  }

  /** Returns true if field gov is set (has been assigned a value) and false otherwise */
  public boolean isSetGov() {
    return EncodingUtils.testBit(__isset_bitfield, __GOV_ISSET_ID);
  }

  public void setGovIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __GOV_ISSET_ID, value);
  }

  /**
   * The dependent token. 0 indexed.
   */
  public int getDep() {
    return this.dep;
  }

  /**
   * The dependent token. 0 indexed.
   */
  public Dependency setDep(int dep) {
    this.dep = dep;
    setDepIsSet(true);
    return this;
  }

  public void unsetDep() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __DEP_ISSET_ID);
  }

  /** Returns true if field dep is set (has been assigned a value) and false otherwise */
  public boolean isSetDep() {
    return EncodingUtils.testBit(__isset_bitfield, __DEP_ISSET_ID);
  }

  public void setDepIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __DEP_ISSET_ID, value);
  }

  /**
   * The relation that holds between gov and dep.
   */
  public String getEdgeType() {
    return this.edgeType;
  }

  /**
   * The relation that holds between gov and dep.
   */
  public Dependency setEdgeType(String edgeType) {
    this.edgeType = edgeType;
    return this;
  }

  public void unsetEdgeType() {
    this.edgeType = null;
  }

  /** Returns true if field edgeType is set (has been assigned a value) and false otherwise */
  public boolean isSetEdgeType() {
    return this.edgeType != null;
  }

  public void setEdgeTypeIsSet(boolean value) {
    if (!value) {
      this.edgeType = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case GOV:
      if (value == null) {
        unsetGov();
      } else {
        setGov((Integer)value);
      }
      break;

    case DEP:
      if (value == null) {
        unsetDep();
      } else {
        setDep((Integer)value);
      }
      break;

    case EDGE_TYPE:
      if (value == null) {
        unsetEdgeType();
      } else {
        setEdgeType((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case GOV:
      return getGov();

    case DEP:
      return getDep();

    case EDGE_TYPE:
      return getEdgeType();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case GOV:
      return isSetGov();
    case DEP:
      return isSetDep();
    case EDGE_TYPE:
      return isSetEdgeType();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Dependency)
      return this.equals((Dependency)that);
    return false;
  }

  public boolean equals(Dependency that) {
    if (that == null)
      return false;

    boolean this_present_gov = true && this.isSetGov();
    boolean that_present_gov = true && that.isSetGov();
    if (this_present_gov || that_present_gov) {
      if (!(this_present_gov && that_present_gov))
        return false;
      if (this.gov != that.gov)
        return false;
    }

    boolean this_present_dep = true;
    boolean that_present_dep = true;
    if (this_present_dep || that_present_dep) {
      if (!(this_present_dep && that_present_dep))
        return false;
      if (this.dep != that.dep)
        return false;
    }

    boolean this_present_edgeType = true && this.isSetEdgeType();
    boolean that_present_edgeType = true && that.isSetEdgeType();
    if (this_present_edgeType || that_present_edgeType) {
      if (!(this_present_edgeType && that_present_edgeType))
        return false;
      if (!this.edgeType.equals(that.edgeType))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_gov = true && (isSetGov());
    list.add(present_gov);
    if (present_gov)
      list.add(gov);

    boolean present_dep = true;
    list.add(present_dep);
    if (present_dep)
      list.add(dep);

    boolean present_edgeType = true && (isSetEdgeType());
    list.add(present_edgeType);
    if (present_edgeType)
      list.add(edgeType);

    return list.hashCode();
  }

  @Override
  public int compareTo(Dependency other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetGov()).compareTo(other.isSetGov());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetGov()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.gov, other.gov);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDep()).compareTo(other.isSetDep());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDep()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.dep, other.dep);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEdgeType()).compareTo(other.isSetEdgeType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEdgeType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.edgeType, other.edgeType);
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
    StringBuilder sb = new StringBuilder("Dependency(");
    boolean first = true;

    if (isSetGov()) {
      sb.append("gov:");
      sb.append(this.gov);
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("dep:");
    sb.append(this.dep);
    first = false;
    if (isSetEdgeType()) {
      if (!first) sb.append(", ");
      sb.append("edgeType:");
      if (this.edgeType == null) {
        sb.append("null");
      } else {
        sb.append(this.edgeType);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'dep' because it's a primitive and you chose the non-beans generator.
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class DependencyStandardSchemeFactory implements SchemeFactory {
    public DependencyStandardScheme getScheme() {
      return new DependencyStandardScheme();
    }
  }

  private static class DependencyStandardScheme extends StandardScheme<Dependency> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Dependency struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // GOV
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.gov = iprot.readI32();
              struct.setGovIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // DEP
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.dep = iprot.readI32();
              struct.setDepIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // EDGE_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.edgeType = iprot.readString();
              struct.setEdgeTypeIsSet(true);
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
      if (!struct.isSetDep()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'dep' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Dependency struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.isSetGov()) {
        oprot.writeFieldBegin(GOV_FIELD_DESC);
        oprot.writeI32(struct.gov);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(DEP_FIELD_DESC);
      oprot.writeI32(struct.dep);
      oprot.writeFieldEnd();
      if (struct.edgeType != null) {
        if (struct.isSetEdgeType()) {
          oprot.writeFieldBegin(EDGE_TYPE_FIELD_DESC);
          oprot.writeString(struct.edgeType);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DependencyTupleSchemeFactory implements SchemeFactory {
    public DependencyTupleScheme getScheme() {
      return new DependencyTupleScheme();
    }
  }

  private static class DependencyTupleScheme extends TupleScheme<Dependency> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Dependency struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.dep);
      BitSet optionals = new BitSet();
      if (struct.isSetGov()) {
        optionals.set(0);
      }
      if (struct.isSetEdgeType()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetGov()) {
        oprot.writeI32(struct.gov);
      }
      if (struct.isSetEdgeType()) {
        oprot.writeString(struct.edgeType);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Dependency struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.dep = iprot.readI32();
      struct.setDepIsSet(true);
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.gov = iprot.readI32();
        struct.setGovIsSet(true);
      }
      if (incoming.get(1)) {
        struct.edgeType = iprot.readString();
        struct.setEdgeTypeIsSet(true);
      }
    }
  }

}

