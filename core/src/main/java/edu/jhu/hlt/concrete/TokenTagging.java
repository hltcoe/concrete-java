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
 * A theory about some token-level annotation.
 * The TokenTagging consists of a mapping from tokens
 * (using token ids) to string tags (e.g. part-of-speech tags or lemmas).
 * 
 * The mapping defined by a TokenTagging may be partial --
 * i.e., some tokens may not be assigned any part of speech tags.
 * 
 * For lattice tokenizations, you may need to create multiple
 * part-of-speech taggings (for different paths through the lattice),
 * since the appropriate tag for a given token may depend on the path
 * taken. For example, you might define a separate
 * TokenTagging for each of the top K paths, which leaves all
 * tokens that are not part of the path unlabeled.
 * 
 * Currently, we use strings to encode annotations. In
 * the future, we may add fields for encoding specific tag sets
 * (eg treebank tags), or for adding compound tags.
 */
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-01-13")
public class TokenTagging implements org.apache.thrift.TBase<TokenTagging, TokenTagging._Fields>, java.io.Serializable, Cloneable, Comparable<TokenTagging> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TokenTagging");

  private static final org.apache.thrift.protocol.TField UUID_FIELD_DESC = new org.apache.thrift.protocol.TField("uuid", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField METADATA_FIELD_DESC = new org.apache.thrift.protocol.TField("metadata", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField TAGGED_TOKEN_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("taggedTokenList", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField TAGGING_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("taggingType", org.apache.thrift.protocol.TType.STRING, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TokenTaggingStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TokenTaggingTupleSchemeFactory());
  }

  private edu.jhu.hlt.concrete.UUID uuid; // required
  private edu.jhu.hlt.concrete.AnnotationMetadata metadata; // required
  private List<TaggedToken> taggedTokenList; // required
  private String taggingType; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * The UUID of this TokenTagging object.
     */
    UUID((short)1, "uuid"),
    /**
     * Information about where the annotation came from.
     * This should be used to tell between gold-standard annotations
     * and automatically-generated theories about the data
     */
    METADATA((short)2, "metadata"),
    /**
     * The mapping from tokens to annotations.
     * This may be a partial mapping.
     */
    TAGGED_TOKEN_LIST((short)3, "taggedTokenList"),
    /**
     * An ontology-backed string that represents the
     * type of token taggings this TokenTagging object
     * produces.
     */
    TAGGING_TYPE((short)4, "taggingType");

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
        case 3: // TAGGED_TOKEN_LIST
          return TAGGED_TOKEN_LIST;
        case 4: // TAGGING_TYPE
          return TAGGING_TYPE;
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
  private static final _Fields optionals[] = {_Fields.TAGGING_TYPE};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.UUID, new org.apache.thrift.meta_data.FieldMetaData("uuid", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, edu.jhu.hlt.concrete.UUID.class)));
    tmpMap.put(_Fields.METADATA, new org.apache.thrift.meta_data.FieldMetaData("metadata", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, edu.jhu.hlt.concrete.AnnotationMetadata.class)));
    tmpMap.put(_Fields.TAGGED_TOKEN_LIST, new org.apache.thrift.meta_data.FieldMetaData("taggedTokenList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TaggedToken.class))));
    tmpMap.put(_Fields.TAGGING_TYPE, new org.apache.thrift.meta_data.FieldMetaData("taggingType", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TokenTagging.class, metaDataMap);
  }

  public TokenTagging() {
  }

  public TokenTagging(
    edu.jhu.hlt.concrete.UUID uuid,
    edu.jhu.hlt.concrete.AnnotationMetadata metadata,
    List<TaggedToken> taggedTokenList)
  {
    this();
    this.uuid = uuid;
    this.metadata = metadata;
    this.taggedTokenList = taggedTokenList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TokenTagging(TokenTagging other) {
    if (other.isSetUuid()) {
      this.uuid = new edu.jhu.hlt.concrete.UUID(other.uuid);
    }
    if (other.isSetMetadata()) {
      this.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata(other.metadata);
    }
    if (other.isSetTaggedTokenList()) {
      List<TaggedToken> __this__taggedTokenList = new ArrayList<TaggedToken>(other.taggedTokenList.size());
      for (TaggedToken other_element : other.taggedTokenList) {
        __this__taggedTokenList.add(new TaggedToken(other_element));
      }
      this.taggedTokenList = __this__taggedTokenList;
    }
    if (other.isSetTaggingType()) {
      this.taggingType = other.taggingType;
    }
  }

  public TokenTagging deepCopy() {
    return new TokenTagging(this);
  }

  @Override
  public void clear() {
    this.uuid = null;
    this.metadata = null;
    this.taggedTokenList = null;
    this.taggingType = null;
  }

  /**
   * The UUID of this TokenTagging object.
   */
  public edu.jhu.hlt.concrete.UUID getUuid() {
    return this.uuid;
  }

  /**
   * The UUID of this TokenTagging object.
   */
  public TokenTagging setUuid(edu.jhu.hlt.concrete.UUID uuid) {
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
   * Information about where the annotation came from.
   * This should be used to tell between gold-standard annotations
   * and automatically-generated theories about the data
   */
  public edu.jhu.hlt.concrete.AnnotationMetadata getMetadata() {
    return this.metadata;
  }

  /**
   * Information about where the annotation came from.
   * This should be used to tell between gold-standard annotations
   * and automatically-generated theories about the data
   */
  public TokenTagging setMetadata(edu.jhu.hlt.concrete.AnnotationMetadata metadata) {
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

  public int getTaggedTokenListSize() {
    return (this.taggedTokenList == null) ? 0 : this.taggedTokenList.size();
  }

  public java.util.Iterator<TaggedToken> getTaggedTokenListIterator() {
    return (this.taggedTokenList == null) ? null : this.taggedTokenList.iterator();
  }

  public void addToTaggedTokenList(TaggedToken elem) {
    if (this.taggedTokenList == null) {
      this.taggedTokenList = new ArrayList<TaggedToken>();
    }
    this.taggedTokenList.add(elem);
  }

  /**
   * The mapping from tokens to annotations.
   * This may be a partial mapping.
   */
  public List<TaggedToken> getTaggedTokenList() {
    return this.taggedTokenList;
  }

  /**
   * The mapping from tokens to annotations.
   * This may be a partial mapping.
   */
  public TokenTagging setTaggedTokenList(List<TaggedToken> taggedTokenList) {
    this.taggedTokenList = taggedTokenList;
    return this;
  }

  public void unsetTaggedTokenList() {
    this.taggedTokenList = null;
  }

  /** Returns true if field taggedTokenList is set (has been assigned a value) and false otherwise */
  public boolean isSetTaggedTokenList() {
    return this.taggedTokenList != null;
  }

  public void setTaggedTokenListIsSet(boolean value) {
    if (!value) {
      this.taggedTokenList = null;
    }
  }

  /**
   * An ontology-backed string that represents the
   * type of token taggings this TokenTagging object
   * produces.
   */
  public String getTaggingType() {
    return this.taggingType;
  }

  /**
   * An ontology-backed string that represents the
   * type of token taggings this TokenTagging object
   * produces.
   */
  public TokenTagging setTaggingType(String taggingType) {
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

    case TAGGED_TOKEN_LIST:
      if (value == null) {
        unsetTaggedTokenList();
      } else {
        setTaggedTokenList((List<TaggedToken>)value);
      }
      break;

    case TAGGING_TYPE:
      if (value == null) {
        unsetTaggingType();
      } else {
        setTaggingType((String)value);
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

    case TAGGED_TOKEN_LIST:
      return getTaggedTokenList();

    case TAGGING_TYPE:
      return getTaggingType();

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
    case TAGGED_TOKEN_LIST:
      return isSetTaggedTokenList();
    case TAGGING_TYPE:
      return isSetTaggingType();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TokenTagging)
      return this.equals((TokenTagging)that);
    return false;
  }

  public boolean equals(TokenTagging that) {
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

    boolean this_present_taggedTokenList = true && this.isSetTaggedTokenList();
    boolean that_present_taggedTokenList = true && that.isSetTaggedTokenList();
    if (this_present_taggedTokenList || that_present_taggedTokenList) {
      if (!(this_present_taggedTokenList && that_present_taggedTokenList))
        return false;
      if (!this.taggedTokenList.equals(that.taggedTokenList))
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

    boolean present_taggedTokenList = true && (isSetTaggedTokenList());
    list.add(present_taggedTokenList);
    if (present_taggedTokenList)
      list.add(taggedTokenList);

    boolean present_taggingType = true && (isSetTaggingType());
    list.add(present_taggingType);
    if (present_taggingType)
      list.add(taggingType);

    return list.hashCode();
  }

  @Override
  public int compareTo(TokenTagging other) {
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
    lastComparison = Boolean.valueOf(isSetTaggedTokenList()).compareTo(other.isSetTaggedTokenList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTaggedTokenList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.taggedTokenList, other.taggedTokenList);
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
    StringBuilder sb = new StringBuilder("TokenTagging(");
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
    sb.append("taggedTokenList:");
    if (this.taggedTokenList == null) {
      sb.append("null");
    } else {
      sb.append(this.taggedTokenList);
    }
    first = false;
    if (isSetTaggingType()) {
      if (!first) sb.append(", ");
      sb.append("taggingType:");
      if (this.taggingType == null) {
        sb.append("null");
      } else {
        sb.append(this.taggingType);
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
    if (taggedTokenList == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'taggedTokenList' was not present! Struct: " + toString());
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

  private static class TokenTaggingStandardSchemeFactory implements SchemeFactory {
    public TokenTaggingStandardScheme getScheme() {
      return new TokenTaggingStandardScheme();
    }
  }

  private static class TokenTaggingStandardScheme extends StandardScheme<TokenTagging> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TokenTagging struct) throws org.apache.thrift.TException {
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
          case 3: // TAGGED_TOKEN_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list24 = iprot.readListBegin();
                struct.taggedTokenList = new ArrayList<TaggedToken>(_list24.size);
                TaggedToken _elem25;
                for (int _i26 = 0; _i26 < _list24.size; ++_i26)
                {
                  _elem25 = new TaggedToken();
                  _elem25.read(iprot);
                  struct.taggedTokenList.add(_elem25);
                }
                iprot.readListEnd();
              }
              struct.setTaggedTokenListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TAGGING_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.taggingType = iprot.readString();
              struct.setTaggingTypeIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TokenTagging struct) throws org.apache.thrift.TException {
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
      if (struct.taggedTokenList != null) {
        oprot.writeFieldBegin(TAGGED_TOKEN_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.taggedTokenList.size()));
          for (TaggedToken _iter27 : struct.taggedTokenList)
          {
            _iter27.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.taggingType != null) {
        if (struct.isSetTaggingType()) {
          oprot.writeFieldBegin(TAGGING_TYPE_FIELD_DESC);
          oprot.writeString(struct.taggingType);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TokenTaggingTupleSchemeFactory implements SchemeFactory {
    public TokenTaggingTupleScheme getScheme() {
      return new TokenTaggingTupleScheme();
    }
  }

  private static class TokenTaggingTupleScheme extends TupleScheme<TokenTagging> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TokenTagging struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      struct.uuid.write(oprot);
      struct.metadata.write(oprot);
      {
        oprot.writeI32(struct.taggedTokenList.size());
        for (TaggedToken _iter28 : struct.taggedTokenList)
        {
          _iter28.write(oprot);
        }
      }
      BitSet optionals = new BitSet();
      if (struct.isSetTaggingType()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetTaggingType()) {
        oprot.writeString(struct.taggingType);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TokenTagging struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.uuid = new edu.jhu.hlt.concrete.UUID();
      struct.uuid.read(iprot);
      struct.setUuidIsSet(true);
      struct.metadata = new edu.jhu.hlt.concrete.AnnotationMetadata();
      struct.metadata.read(iprot);
      struct.setMetadataIsSet(true);
      {
        org.apache.thrift.protocol.TList _list29 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.taggedTokenList = new ArrayList<TaggedToken>(_list29.size);
        TaggedToken _elem30;
        for (int _i31 = 0; _i31 < _list29.size; ++_i31)
        {
          _elem30 = new TaggedToken();
          _elem30.read(iprot);
          struct.taggedTokenList.add(_elem30);
        }
      }
      struct.setTaggedTokenListIsSet(true);
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.taggingType = iprot.readString();
        struct.setTaggingTypeIsSet(true);
      }
    }
  }

}
