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
 * A structure that represents entity linking annotations.
 */
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)")
public class Linking implements org.apache.thrift.TBase<Linking, Linking._Fields>, java.io.Serializable, Cloneable, Comparable<Linking> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Linking");

  private static final org.apache.thrift.protocol.TField METADATA_FIELD_DESC = new org.apache.thrift.protocol.TField("metadata", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField LINK_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("linkList", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new LinkingStandardSchemeFactory());
    schemes.put(TupleScheme.class, new LinkingTupleSchemeFactory());
  }

  private edu.jhu.hlt.concrete.AnnotationMetadata metadata; // required
  private List<Link> linkList; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * Metadata related to this Linking object.
     */
    METADATA((short)1, "metadata"),
    /**
     * A list of Link objects that this Linking object contains.
     */
    LINK_LIST((short)2, "linkList");

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
        case 1: // METADATA
          return METADATA;
        case 2: // LINK_LIST
          return LINK_LIST;
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
    tmpMap.put(_Fields.METADATA, new org.apache.thrift.meta_data.FieldMetaData("metadata", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, edu.jhu.hlt.concrete.AnnotationMetadata.class)));
    tmpMap.put(_Fields.LINK_LIST, new org.apache.thrift.meta_data.FieldMetaData("linkList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Link.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Linking.class, metaDataMap);
  }

  public Linking() {
  }

  public Linking(
    edu.jhu.hlt.concrete.AnnotationMetadata metadata,
    List<Link> linkList)
  {
    this();
    this.metadata = metadata;
    this.linkList = linkList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Linking(Linking other) {
    if (other.isSetMetadata()) {
      this.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata(other.metadata);
    }
    if (other.isSetLinkList()) {
      List<Link> __this__linkList = new ArrayList<Link>(other.linkList.size());
      for (Link other_element : other.linkList) {
        __this__linkList.add(new Link(other_element));
      }
      this.linkList = __this__linkList;
    }
  }

  public Linking deepCopy() {
    return new Linking(this);
  }

  @Override
  public void clear() {
    this.metadata = null;
    this.linkList = null;
  }

  /**
   * Metadata related to this Linking object.
   */
  public edu.jhu.hlt.concrete.AnnotationMetadata getMetadata() {
    return this.metadata;
  }

  /**
   * Metadata related to this Linking object.
   */
  public Linking setMetadata(edu.jhu.hlt.concrete.AnnotationMetadata metadata) {
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

  public int getLinkListSize() {
    return (this.linkList == null) ? 0 : this.linkList.size();
  }

  public java.util.Iterator<Link> getLinkListIterator() {
    return (this.linkList == null) ? null : this.linkList.iterator();
  }

  public void addToLinkList(Link elem) {
    if (this.linkList == null) {
      this.linkList = new ArrayList<Link>();
    }
    this.linkList.add(elem);
  }

  /**
   * A list of Link objects that this Linking object contains.
   */
  public List<Link> getLinkList() {
    return this.linkList;
  }

  /**
   * A list of Link objects that this Linking object contains.
   */
  public Linking setLinkList(List<Link> linkList) {
    this.linkList = linkList;
    return this;
  }

  public void unsetLinkList() {
    this.linkList = null;
  }

  /** Returns true if field linkList is set (has been assigned a value) and false otherwise */
  public boolean isSetLinkList() {
    return this.linkList != null;
  }

  public void setLinkListIsSet(boolean value) {
    if (!value) {
      this.linkList = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case METADATA:
      if (value == null) {
        unsetMetadata();
      } else {
        setMetadata((edu.jhu.hlt.concrete.AnnotationMetadata)value);
      }
      break;

    case LINK_LIST:
      if (value == null) {
        unsetLinkList();
      } else {
        setLinkList((List<Link>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case METADATA:
      return getMetadata();

    case LINK_LIST:
      return getLinkList();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case METADATA:
      return isSetMetadata();
    case LINK_LIST:
      return isSetLinkList();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Linking)
      return this.equals((Linking)that);
    return false;
  }

  public boolean equals(Linking that) {
    if (that == null)
      return false;

    boolean this_present_metadata = true && this.isSetMetadata();
    boolean that_present_metadata = true && that.isSetMetadata();
    if (this_present_metadata || that_present_metadata) {
      if (!(this_present_metadata && that_present_metadata))
        return false;
      if (!this.metadata.equals(that.metadata))
        return false;
    }

    boolean this_present_linkList = true && this.isSetLinkList();
    boolean that_present_linkList = true && that.isSetLinkList();
    if (this_present_linkList || that_present_linkList) {
      if (!(this_present_linkList && that_present_linkList))
        return false;
      if (!this.linkList.equals(that.linkList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_metadata = true && (isSetMetadata());
    list.add(present_metadata);
    if (present_metadata)
      list.add(metadata);

    boolean present_linkList = true && (isSetLinkList());
    list.add(present_linkList);
    if (present_linkList)
      list.add(linkList);

    return list.hashCode();
  }

  @Override
  public int compareTo(Linking other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

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
    lastComparison = Boolean.valueOf(isSetLinkList()).compareTo(other.isSetLinkList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLinkList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.linkList, other.linkList);
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
    StringBuilder sb = new StringBuilder("Linking(");
    boolean first = true;

    sb.append("metadata:");
    if (this.metadata == null) {
      sb.append("null");
    } else {
      sb.append(this.metadata);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("linkList:");
    if (this.linkList == null) {
      sb.append("null");
    } else {
      sb.append(this.linkList);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (metadata == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'metadata' was not present! Struct: " + toString());
    }
    if (linkList == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'linkList' was not present! Struct: " + toString());
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class LinkingStandardSchemeFactory implements SchemeFactory {
    public LinkingStandardScheme getScheme() {
      return new LinkingStandardScheme();
    }
  }

  private static class LinkingStandardScheme extends StandardScheme<Linking> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Linking struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // METADATA
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata();
              struct.metadata.read(iprot);
              struct.setMetadataIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // LINK_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.linkList = new ArrayList<Link>(_list8.size);
                Link _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = new Link();
                  _elem9.read(iprot);
                  struct.linkList.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setLinkListIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Linking struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.metadata != null) {
        oprot.writeFieldBegin(METADATA_FIELD_DESC);
        struct.metadata.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.linkList != null) {
        oprot.writeFieldBegin(LINK_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.linkList.size()));
          for (Link _iter11 : struct.linkList)
          {
            _iter11.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class LinkingTupleSchemeFactory implements SchemeFactory {
    public LinkingTupleScheme getScheme() {
      return new LinkingTupleScheme();
    }
  }

  private static class LinkingTupleScheme extends TupleScheme<Linking> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Linking struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      struct.metadata.write(oprot);
      {
        oprot.writeI32(struct.linkList.size());
        for (Link _iter12 : struct.linkList)
        {
          _iter12.write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Linking struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata();
      struct.metadata.read(iprot);
      struct.setMetadataIsSet(true);
      {
        org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.linkList = new ArrayList<Link>(_list13.size);
        Link _elem14;
        for (int _i15 = 0; _i15 < _list13.size; ++_i15)
        {
          _elem14 = new Link();
          _elem14.read(iprot);
          struct.linkList.add(_elem14);
        }
      }
      struct.setLinkListIsSet(true);
    }
  }

}

