/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package edu.jhu.hlt.concrete.services;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
/**
 * An exception to be used with Concrete thrift
 * services.
 */
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)")
public class ConcreteThriftException extends org.apache.thrift.TException implements org.apache.thrift.TBase<ConcreteThriftException, ConcreteThriftException._Fields>, java.io.Serializable, Cloneable, Comparable<ConcreteThriftException> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ConcreteThriftException");

  private static final org.apache.thrift.protocol.TField MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("message", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField SER_EX_FIELD_DESC = new org.apache.thrift.protocol.TField("serEx", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ConcreteThriftExceptionStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ConcreteThriftExceptionTupleSchemeFactory();

  private java.lang.String message; // required
  private java.nio.ByteBuffer serEx; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MESSAGE((short)1, "message"),
    SER_EX((short)2, "serEx");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // MESSAGE
          return MESSAGE;
        case 2: // SER_EX
          return SER_EX;
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
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final _Fields optionals[] = {_Fields.SER_EX};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("message", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SER_EX, new org.apache.thrift.meta_data.FieldMetaData("serEx", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ConcreteThriftException.class, metaDataMap);
  }

  public ConcreteThriftException() {
  }

  public ConcreteThriftException(
    java.lang.String message)
  {
    this();
    this.message = message;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ConcreteThriftException(ConcreteThriftException other) {
    if (other.isSetMessage()) {
      this.message = other.message;
    }
    if (other.isSetSerEx()) {
      this.serEx = org.apache.thrift.TBaseHelper.copyBinary(other.serEx);
    }
  }

  public ConcreteThriftException deepCopy() {
    return new ConcreteThriftException(this);
  }

  @Override
  public void clear() {
    this.message = null;
    this.serEx = null;
  }

  public java.lang.String getMessage() {
    return this.message;
  }

  public ConcreteThriftException setMessage(java.lang.String message) {
    this.message = message;
    return this;
  }

  public void unsetMessage() {
    this.message = null;
  }

  /** Returns true if field message is set (has been assigned a value) and false otherwise */
  public boolean isSetMessage() {
    return this.message != null;
  }

  public void setMessageIsSet(boolean value) {
    if (!value) {
      this.message = null;
    }
  }

  public byte[] getSerEx() {
    setSerEx(org.apache.thrift.TBaseHelper.rightSize(serEx));
    return serEx == null ? null : serEx.array();
  }

  public java.nio.ByteBuffer bufferForSerEx() {
    return org.apache.thrift.TBaseHelper.copyBinary(serEx);
  }

  public ConcreteThriftException setSerEx(byte[] serEx) {
    this.serEx = serEx == null ? (java.nio.ByteBuffer)null : java.nio.ByteBuffer.wrap(serEx.clone());
    return this;
  }

  public ConcreteThriftException setSerEx(java.nio.ByteBuffer serEx) {
    this.serEx = org.apache.thrift.TBaseHelper.copyBinary(serEx);
    return this;
  }

  public void unsetSerEx() {
    this.serEx = null;
  }

  /** Returns true if field serEx is set (has been assigned a value) and false otherwise */
  public boolean isSetSerEx() {
    return this.serEx != null;
  }

  public void setSerExIsSet(boolean value) {
    if (!value) {
      this.serEx = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case MESSAGE:
      if (value == null) {
        unsetMessage();
      } else {
        setMessage((java.lang.String)value);
      }
      break;

    case SER_EX:
      if (value == null) {
        unsetSerEx();
      } else {
        if (value instanceof byte[]) {
          setSerEx((byte[])value);
        } else {
          setSerEx((java.nio.ByteBuffer)value);
        }
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case MESSAGE:
      return getMessage();

    case SER_EX:
      return getSerEx();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case MESSAGE:
      return isSetMessage();
    case SER_EX:
      return isSetSerEx();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof ConcreteThriftException)
      return this.equals((ConcreteThriftException)that);
    return false;
  }

  public boolean equals(ConcreteThriftException that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_message = true && this.isSetMessage();
    boolean that_present_message = true && that.isSetMessage();
    if (this_present_message || that_present_message) {
      if (!(this_present_message && that_present_message))
        return false;
      if (!this.message.equals(that.message))
        return false;
    }

    boolean this_present_serEx = true && this.isSetSerEx();
    boolean that_present_serEx = true && that.isSetSerEx();
    if (this_present_serEx || that_present_serEx) {
      if (!(this_present_serEx && that_present_serEx))
        return false;
      if (!this.serEx.equals(that.serEx))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetMessage()) ? 131071 : 524287);
    if (isSetMessage())
      hashCode = hashCode * 8191 + message.hashCode();

    hashCode = hashCode * 8191 + ((isSetSerEx()) ? 131071 : 524287);
    if (isSetSerEx())
      hashCode = hashCode * 8191 + serEx.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(ConcreteThriftException other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetMessage()).compareTo(other.isSetMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.message, other.message);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetSerEx()).compareTo(other.isSetSerEx());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSerEx()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.serEx, other.serEx);
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
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("ConcreteThriftException(");
    boolean first = true;

    sb.append("message:");
    if (this.message == null) {
      sb.append("null");
    } else {
      sb.append(this.message);
    }
    first = false;
    if (isSetSerEx()) {
      if (!first) sb.append(", ");
      sb.append("serEx:");
      if (this.serEx == null) {
        sb.append("null");
      } else {
        org.apache.thrift.TBaseHelper.toString(this.serEx, sb);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (message == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'message' was not present! Struct: " + toString());
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

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ConcreteThriftExceptionStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ConcreteThriftExceptionStandardScheme getScheme() {
      return new ConcreteThriftExceptionStandardScheme();
    }
  }

  private static class ConcreteThriftExceptionStandardScheme extends org.apache.thrift.scheme.StandardScheme<ConcreteThriftException> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ConcreteThriftException struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.message = iprot.readString();
              struct.setMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SER_EX
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.serEx = iprot.readBinary();
              struct.setSerExIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ConcreteThriftException struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.message != null) {
        oprot.writeFieldBegin(MESSAGE_FIELD_DESC);
        oprot.writeString(struct.message);
        oprot.writeFieldEnd();
      }
      if (struct.serEx != null) {
        if (struct.isSetSerEx()) {
          oprot.writeFieldBegin(SER_EX_FIELD_DESC);
          oprot.writeBinary(struct.serEx);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ConcreteThriftExceptionTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ConcreteThriftExceptionTupleScheme getScheme() {
      return new ConcreteThriftExceptionTupleScheme();
    }
  }

  private static class ConcreteThriftExceptionTupleScheme extends org.apache.thrift.scheme.TupleScheme<ConcreteThriftException> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ConcreteThriftException struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeString(struct.message);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetSerEx()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetSerEx()) {
        oprot.writeBinary(struct.serEx);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ConcreteThriftException struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.message = iprot.readString();
      struct.setMessageIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.serEx = iprot.readBinary();
        struct.setSerExIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

