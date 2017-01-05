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
 * A structure that represents a 'tagging' of a Communication. These
 * might be labels or annotations on a particular communcation.
 * 
 * For example, this structure might be used to describe the topics
 * discussed in a Communication. The taggingType might be 'topic', and
 * the tagList might include 'politics' and 'science'.
 */
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-01-04")
public class CommunicationTagging implements org.apache.thrift.TBase<CommunicationTagging, CommunicationTagging._Fields>, java.io.Serializable, Cloneable, Comparable<CommunicationTagging> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("CommunicationTagging");

  private static final org.apache.thrift.protocol.TField UUID_FIELD_DESC = new org.apache.thrift.protocol.TField("uuid", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField METADATA_FIELD_DESC = new org.apache.thrift.protocol.TField("metadata", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField TAGGING_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("taggingType", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField TAG_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("tagList", org.apache.thrift.protocol.TType.LIST, (short)4);
  private static final org.apache.thrift.protocol.TField CONFIDENCE_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("confidenceList", org.apache.thrift.protocol.TType.LIST, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new CommunicationTaggingStandardSchemeFactory());
    schemes.put(TupleScheme.class, new CommunicationTaggingTupleSchemeFactory());
  }

  private edu.jhu.hlt.concrete.UUID uuid; // required
  private edu.jhu.hlt.concrete.AnnotationMetadata metadata; // required
  private String taggingType; // required
  private List<String> tagList; // optional
  private List<Double> confidenceList; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * A unique identifier for this CommunicationTagging object.
     */
    UUID((short)1, "uuid"),
    /**
     * AnnotationMetadata to support this CommunicationTagging object.
     */
    METADATA((short)2, "metadata"),
    /**
     * A string that captures the type of this CommunicationTagging
     * object. For example: 'topic' or 'gender'.
     */
    TAGGING_TYPE((short)3, "taggingType"),
    /**
     * A list of strings that represent different tags related to the taggingType.
     * For example, if the taggingType is 'topic', some example tags might be
     * 'politics', 'science', etc.
     */
    TAG_LIST((short)4, "tagList"),
    /**
     * A list of doubles, parallel to the list of strings in tagList,
     * that indicate the confidences of each tag.
     */
    CONFIDENCE_LIST((short)5, "confidenceList");

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
        case 1: // UUID
          return UUID;
        case 2: // METADATA
          return METADATA;
        case 3: // TAGGING_TYPE
          return TAGGING_TYPE;
        case 4: // TAG_LIST
          return TAG_LIST;
        case 5: // CONFIDENCE_LIST
          return CONFIDENCE_LIST;
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
  private static final _Fields optionals[] = {_Fields.TAG_LIST,_Fields.CONFIDENCE_LIST};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.UUID, new org.apache.thrift.meta_data.FieldMetaData("uuid", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, edu.jhu.hlt.concrete.UUID.class)));
    tmpMap.put(_Fields.METADATA, new org.apache.thrift.meta_data.FieldMetaData("metadata", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, edu.jhu.hlt.concrete.AnnotationMetadata.class)));
    tmpMap.put(_Fields.TAGGING_TYPE, new org.apache.thrift.meta_data.FieldMetaData("taggingType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TAG_LIST, new org.apache.thrift.meta_data.FieldMetaData("tagList", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.CONFIDENCE_LIST, new org.apache.thrift.meta_data.FieldMetaData("confidenceList", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(CommunicationTagging.class, metaDataMap);
  }

  public CommunicationTagging() {
  }

  public CommunicationTagging(
    edu.jhu.hlt.concrete.UUID uuid,
    edu.jhu.hlt.concrete.AnnotationMetadata metadata,
    String taggingType)
  {
    this();
    this.uuid = uuid;
    this.metadata = metadata;
    this.taggingType = taggingType;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public CommunicationTagging(CommunicationTagging other) {
    if (other.isSetUuid()) {
      this.uuid = new edu.jhu.hlt.concrete.UUID(other.uuid);
    }
    if (other.isSetMetadata()) {
      this.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata(other.metadata);
    }
    if (other.isSetTaggingType()) {
      this.taggingType = other.taggingType;
    }
    if (other.isSetTagList()) {
      List<String> __this__tagList = new ArrayList<String>(other.tagList);
      this.tagList = __this__tagList;
    }
    if (other.isSetConfidenceList()) {
      List<Double> __this__confidenceList = new ArrayList<Double>(other.confidenceList);
      this.confidenceList = __this__confidenceList;
    }
  }

  public CommunicationTagging deepCopy() {
    return new CommunicationTagging(this);
  }

  @Override
  public void clear() {
    this.uuid = null;
    this.metadata = null;
    this.taggingType = null;
    this.tagList = null;
    this.confidenceList = null;
  }

  /**
   * A unique identifier for this CommunicationTagging object.
   */
  public edu.jhu.hlt.concrete.UUID getUuid() {
    return this.uuid;
  }

  /**
   * A unique identifier for this CommunicationTagging object.
   */
  public CommunicationTagging setUuid(edu.jhu.hlt.concrete.UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  public void unsetUuid() {
    this.uuid = null;
  }

  /** Returns true if field uuid is set (has been assigned a value) and false otherwise */
  public boolean isSetUuid() {
    return this.uuid != null;
  }

  public void setUuidIsSet(boolean value) {
    if (!value) {
      this.uuid = null;
    }
  }

  /**
   * AnnotationMetadata to support this CommunicationTagging object.
   */
  public edu.jhu.hlt.concrete.AnnotationMetadata getMetadata() {
    return this.metadata;
  }

  /**
   * AnnotationMetadata to support this CommunicationTagging object.
   */
  public CommunicationTagging setMetadata(edu.jhu.hlt.concrete.AnnotationMetadata metadata) {
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
   * A string that captures the type of this CommunicationTagging
   * object. For example: 'topic' or 'gender'.
   */
  public String getTaggingType() {
    return this.taggingType;
  }

  /**
   * A string that captures the type of this CommunicationTagging
   * object. For example: 'topic' or 'gender'.
   */
  public CommunicationTagging setTaggingType(String taggingType) {
    this.taggingType = taggingType;
    return this;
  }

  public void unsetTaggingType() {
    this.taggingType = null;
  }

  /** Returns true if field taggingType is set (has been assigned a value) and false otherwise */
  public boolean isSetTaggingType() {
    return this.taggingType != null;
  }

  public void setTaggingTypeIsSet(boolean value) {
    if (!value) {
      this.taggingType = null;
    }
  }

  public int getTagListSize() {
    return (this.tagList == null) ? 0 : this.tagList.size();
  }

  public java.util.Iterator<String> getTagListIterator() {
    return (this.tagList == null) ? null : this.tagList.iterator();
  }

  public void addToTagList(String elem) {
    if (this.tagList == null) {
      this.tagList = new ArrayList<String>();
    }
    this.tagList.add(elem);
  }

  /**
   * A list of strings that represent different tags related to the taggingType.
   * For example, if the taggingType is 'topic', some example tags might be
   * 'politics', 'science', etc.
   */
  public List<String> getTagList() {
    return this.tagList;
  }

  /**
   * A list of strings that represent different tags related to the taggingType.
   * For example, if the taggingType is 'topic', some example tags might be
   * 'politics', 'science', etc.
   */
  public CommunicationTagging setTagList(List<String> tagList) {
    this.tagList = tagList;
    return this;
  }

  public void unsetTagList() {
    this.tagList = null;
  }

  /** Returns true if field tagList is set (has been assigned a value) and false otherwise */
  public boolean isSetTagList() {
    return this.tagList != null;
  }

  public void setTagListIsSet(boolean value) {
    if (!value) {
      this.tagList = null;
    }
  }

  public int getConfidenceListSize() {
    return (this.confidenceList == null) ? 0 : this.confidenceList.size();
  }

  public java.util.Iterator<Double> getConfidenceListIterator() {
    return (this.confidenceList == null) ? null : this.confidenceList.iterator();
  }

  public void addToConfidenceList(double elem) {
    if (this.confidenceList == null) {
      this.confidenceList = new ArrayList<Double>();
    }
    this.confidenceList.add(elem);
  }

  /**
   * A list of doubles, parallel to the list of strings in tagList,
   * that indicate the confidences of each tag.
   */
  public List<Double> getConfidenceList() {
    return this.confidenceList;
  }

  /**
   * A list of doubles, parallel to the list of strings in tagList,
   * that indicate the confidences of each tag.
   */
  public CommunicationTagging setConfidenceList(List<Double> confidenceList) {
    this.confidenceList = confidenceList;
    return this;
  }

  public void unsetConfidenceList() {
    this.confidenceList = null;
  }

  /** Returns true if field confidenceList is set (has been assigned a value) and false otherwise */
  public boolean isSetConfidenceList() {
    return this.confidenceList != null;
  }

  public void setConfidenceListIsSet(boolean value) {
    if (!value) {
      this.confidenceList = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case UUID:
      if (value == null) {
        unsetUuid();
      } else {
        setUuid((edu.jhu.hlt.concrete.UUID)value);
      }
      break;

    case METADATA:
      if (value == null) {
        unsetMetadata();
      } else {
        setMetadata((edu.jhu.hlt.concrete.AnnotationMetadata)value);
      }
      break;

    case TAGGING_TYPE:
      if (value == null) {
        unsetTaggingType();
      } else {
        setTaggingType((String)value);
      }
      break;

    case TAG_LIST:
      if (value == null) {
        unsetTagList();
      } else {
        setTagList((List<String>)value);
      }
      break;

    case CONFIDENCE_LIST:
      if (value == null) {
        unsetConfidenceList();
      } else {
        setConfidenceList((List<Double>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case UUID:
      return getUuid();

    case METADATA:
      return getMetadata();

    case TAGGING_TYPE:
      return getTaggingType();

    case TAG_LIST:
      return getTagList();

    case CONFIDENCE_LIST:
      return getConfidenceList();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case UUID:
      return isSetUuid();
    case METADATA:
      return isSetMetadata();
    case TAGGING_TYPE:
      return isSetTaggingType();
    case TAG_LIST:
      return isSetTagList();
    case CONFIDENCE_LIST:
      return isSetConfidenceList();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof CommunicationTagging)
      return this.equals((CommunicationTagging)that);
    return false;
  }

  public boolean equals(CommunicationTagging that) {
    if (that == null)
      return false;

    boolean this_present_uuid = true && this.isSetUuid();
    boolean that_present_uuid = true && that.isSetUuid();
    if (this_present_uuid || that_present_uuid) {
      if (!(this_present_uuid && that_present_uuid))
        return false;
      if (!this.uuid.equals(that.uuid))
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

    boolean this_present_taggingType = true && this.isSetTaggingType();
    boolean that_present_taggingType = true && that.isSetTaggingType();
    if (this_present_taggingType || that_present_taggingType) {
      if (!(this_present_taggingType && that_present_taggingType))
        return false;
      if (!this.taggingType.equals(that.taggingType))
        return false;
    }

    boolean this_present_tagList = true && this.isSetTagList();
    boolean that_present_tagList = true && that.isSetTagList();
    if (this_present_tagList || that_present_tagList) {
      if (!(this_present_tagList && that_present_tagList))
        return false;
      if (!this.tagList.equals(that.tagList))
        return false;
    }

    boolean this_present_confidenceList = true && this.isSetConfidenceList();
    boolean that_present_confidenceList = true && that.isSetConfidenceList();
    if (this_present_confidenceList || that_present_confidenceList) {
      if (!(this_present_confidenceList && that_present_confidenceList))
        return false;
      if (!this.confidenceList.equals(that.confidenceList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_uuid = true && (isSetUuid());
    list.add(present_uuid);
    if (present_uuid)
      list.add(uuid);

    boolean present_metadata = true && (isSetMetadata());
    list.add(present_metadata);
    if (present_metadata)
      list.add(metadata);

    boolean present_taggingType = true && (isSetTaggingType());
    list.add(present_taggingType);
    if (present_taggingType)
      list.add(taggingType);

    boolean present_tagList = true && (isSetTagList());
    list.add(present_tagList);
    if (present_tagList)
      list.add(tagList);

    boolean present_confidenceList = true && (isSetConfidenceList());
    list.add(present_confidenceList);
    if (present_confidenceList)
      list.add(confidenceList);

    return list.hashCode();
  }

  @Override
  public int compareTo(CommunicationTagging other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetUuid()).compareTo(other.isSetUuid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUuid()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.uuid, other.uuid);
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
    lastComparison = Boolean.valueOf(isSetTaggingType()).compareTo(other.isSetTaggingType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTaggingType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.taggingType, other.taggingType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTagList()).compareTo(other.isSetTagList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTagList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tagList, other.tagList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetConfidenceList()).compareTo(other.isSetConfidenceList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetConfidenceList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.confidenceList, other.confidenceList);
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
    StringBuilder sb = new StringBuilder("CommunicationTagging(");
    boolean first = true;

    sb.append("uuid:");
    if (this.uuid == null) {
      sb.append("null");
    } else {
      sb.append(this.uuid);
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
    if (!first) sb.append(", ");
    sb.append("taggingType:");
    if (this.taggingType == null) {
      sb.append("null");
    } else {
      sb.append(this.taggingType);
    }
    first = false;
    if (isSetTagList()) {
      if (!first) sb.append(", ");
      sb.append("tagList:");
      if (this.tagList == null) {
        sb.append("null");
      } else {
        sb.append(this.tagList);
      }
      first = false;
    }
    if (isSetConfidenceList()) {
      if (!first) sb.append(", ");
      sb.append("confidenceList:");
      if (this.confidenceList == null) {
        sb.append("null");
      } else {
        sb.append(this.confidenceList);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (uuid == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'uuid' was not present! Struct: " + toString());
    }
    if (metadata == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'metadata' was not present! Struct: " + toString());
    }
    if (taggingType == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'taggingType' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (uuid != null) {
      uuid.validate();
    }
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

  private static class CommunicationTaggingStandardSchemeFactory implements SchemeFactory {
    public CommunicationTaggingStandardScheme getScheme() {
      return new CommunicationTaggingStandardScheme();
    }
  }

  private static class CommunicationTaggingStandardScheme extends StandardScheme<CommunicationTagging> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, CommunicationTagging struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // UUID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.uuid = new edu.jhu.hlt.concrete.UUID();
              struct.uuid.read(iprot);
              struct.setUuidIsSet(true);
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
          case 3: // TAGGING_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.taggingType = iprot.readString();
              struct.setTaggingTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TAG_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.tagList = new ArrayList<String>(_list0.size);
                String _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readString();
                  struct.tagList.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setTagListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // CONFIDENCE_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list3 = iprot.readListBegin();
                struct.confidenceList = new ArrayList<Double>(_list3.size);
                double _elem4;
                for (int _i5 = 0; _i5 < _list3.size; ++_i5)
                {
                  _elem4 = iprot.readDouble();
                  struct.confidenceList.add(_elem4);
                }
                iprot.readListEnd();
              }
              struct.setConfidenceListIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, CommunicationTagging struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.uuid != null) {
        oprot.writeFieldBegin(UUID_FIELD_DESC);
        struct.uuid.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.metadata != null) {
        oprot.writeFieldBegin(METADATA_FIELD_DESC);
        struct.metadata.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.taggingType != null) {
        oprot.writeFieldBegin(TAGGING_TYPE_FIELD_DESC);
        oprot.writeString(struct.taggingType);
        oprot.writeFieldEnd();
      }
      if (struct.tagList != null) {
        if (struct.isSetTagList()) {
          oprot.writeFieldBegin(TAG_LIST_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.tagList.size()));
            for (String _iter6 : struct.tagList)
            {
              oprot.writeString(_iter6);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.confidenceList != null) {
        if (struct.isSetConfidenceList()) {
          oprot.writeFieldBegin(CONFIDENCE_LIST_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, struct.confidenceList.size()));
            for (double _iter7 : struct.confidenceList)
            {
              oprot.writeDouble(_iter7);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class CommunicationTaggingTupleSchemeFactory implements SchemeFactory {
    public CommunicationTaggingTupleScheme getScheme() {
      return new CommunicationTaggingTupleScheme();
    }
  }

  private static class CommunicationTaggingTupleScheme extends TupleScheme<CommunicationTagging> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, CommunicationTagging struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      struct.uuid.write(oprot);
      struct.metadata.write(oprot);
      oprot.writeString(struct.taggingType);
      BitSet optionals = new BitSet();
      if (struct.isSetTagList()) {
        optionals.set(0);
      }
      if (struct.isSetConfidenceList()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetTagList()) {
        {
          oprot.writeI32(struct.tagList.size());
          for (String _iter8 : struct.tagList)
          {
            oprot.writeString(_iter8);
          }
        }
      }
      if (struct.isSetConfidenceList()) {
        {
          oprot.writeI32(struct.confidenceList.size());
          for (double _iter9 : struct.confidenceList)
          {
            oprot.writeDouble(_iter9);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, CommunicationTagging struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.uuid = new edu.jhu.hlt.concrete.UUID();
      struct.uuid.read(iprot);
      struct.setUuidIsSet(true);
      struct.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata();
      struct.metadata.read(iprot);
      struct.setMetadataIsSet(true);
      struct.taggingType = iprot.readString();
      struct.setTaggingTypeIsSet(true);
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list10 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.tagList = new ArrayList<String>(_list10.size);
          String _elem11;
          for (int _i12 = 0; _i12 < _list10.size; ++_i12)
          {
            _elem11 = iprot.readString();
            struct.tagList.add(_elem11);
          }
        }
        struct.setTagListIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, iprot.readI32());
          struct.confidenceList = new ArrayList<Double>(_list13.size);
          double _elem14;
          for (int _i15 = 0; _i15 < _list13.size; ++_i15)
          {
            _elem14 = iprot.readDouble();
            struct.confidenceList.add(_elem14);
          }
        }
        struct.setConfidenceListIsSet(true);
      }
    }
  }

}

