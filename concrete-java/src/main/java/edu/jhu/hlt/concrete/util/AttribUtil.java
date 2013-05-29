/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;

public class AttribUtil {

    private static final Descriptor VERTEX_KIND_ATTRIBUTE = Concrete.VertexKindAttribute.getDescriptor();
    private static final Descriptor MENTION_ATTRIBUTE = Concrete.MentionAttribute.getDescriptor();
    private static final Descriptor BOOLEAN_ATTRIBUTE = Concrete.BooleanAttribute.getDescriptor();
    private static final Descriptor STRING_ATTRIBUTE = Concrete.StringAttribute.getDescriptor();
    private static final Descriptor FLOAT_ATTRIBUTE = Concrete.FloatAttribute.getDescriptor();
    private static final Descriptor INT32_ATTRIBUTE = Concrete.Int32Attribute.getDescriptor();
    private static final Descriptor INT64_ATTRIBUTE = Concrete.Int64Attribute.getDescriptor();
    private static final Descriptor COMMUNICATION_GUID_ATTRIBUTE = Concrete.CommunicationGUIDAttribute.getDescriptor();
    private static final Descriptor STRING_FLOAT_MAP_ATTRIBUTE = Concrete.StringFloatMapAttribute.getDescriptor();

    // private static final Descriptor Xx_ATTRIBUTE =
    // Concrete.XxAttribute.getDescriptor();

    public static Message buildAttribute(FieldDescriptor field, Object value, String toolName) throws ConcreteException {
        return buildAttribute(field, value, buildMetadata(toolName));
    }

    public static Message buildAttribute(FieldDescriptor field, Object value, String toolName, float confidence) throws ConcreteException {
        return buildAttribute(field, value, buildMetadata(toolName, confidence));
    }

    public static Message buildAttribute(FieldDescriptor field, Object value, Concrete.AttributeMetadata metadata) 
            throws ConcreteException {
        if (field == null)
            throw new ConcreteException("FieldDescriptor is NULL!  This probably means that you had a typo "
                    + "in a call to FieldDescriptor.findFieldByName.");
        Descriptor attribDescriptor = field.getMessageType();
        if (attribDescriptor == MENTION_ATTRIBUTE) {
            return Concrete.MentionAttribute.newBuilder().setMetadata(metadata).setValue((Concrete.EntityMentionRef) value)
                    .setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == BOOLEAN_ATTRIBUTE) {
            return Concrete.BooleanAttribute.newBuilder().setMetadata(metadata).setValue((Boolean) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == STRING_ATTRIBUTE) {
            return Concrete.StringAttribute.newBuilder().setMetadata(metadata).setValue((String) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == FLOAT_ATTRIBUTE) {
            return Concrete.FloatAttribute.newBuilder().setMetadata(metadata).setValue((Float) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == INT32_ATTRIBUTE) {
            return Concrete.Int32Attribute.newBuilder().setMetadata(metadata).setValue((Integer) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == INT64_ATTRIBUTE) {
            return Concrete.Int64Attribute.newBuilder().setMetadata(metadata).setValue((Long) value).setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == COMMUNICATION_GUID_ATTRIBUTE) {
            return Concrete.CommunicationGUIDAttribute.newBuilder().setMetadata(metadata).setValue((Concrete.CommunicationGUID) value)
                    .setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == STRING_FLOAT_MAP_ATTRIBUTE) {
            return Concrete.StringFloatMapAttribute.newBuilder().setMetadata(metadata).addAllValue(buildStringFloatMap(value))
                    .setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == VERTEX_KIND_ATTRIBUTE) {
            return Concrete.VertexKindAttribute.newBuilder().setMetadata(metadata).setValue((Concrete.Vertex.Kind) value)
                    .setUuid(IdUtil.generateUUID()).build();
        } else {
            throw new ConcreteException("Unknown attribute type");
        }
    }

    private static List<Concrete.StringFloatMapAttribute.Entry> buildStringFloatMap(Object object) throws ConcreteException {
        try {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) object;
            List<Concrete.StringFloatMapAttribute.Entry> result = new ArrayList<Concrete.StringFloatMapAttribute.Entry>(map.size());
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                result.add(Concrete.StringFloatMapAttribute.Entry.newBuilder().setKey((String) entry.getKey())
                        .setValue((Float) entry.getValue()).build());
            }
            return result;
        } catch (ClassCastException e) {
            throw new ConcreteException("Expected a Map<String,Float>!");
        }
    }

    public static Concrete.AttributeMetadata buildMetadata(String toolName, float confidence) {
        return Concrete.AttributeMetadata.newBuilder().setTool(toolName).setConfidence(confidence).build();
    }

    public static Concrete.AttributeMetadata buildMetadata(String toolName) {
        return Concrete.AttributeMetadata.newBuilder().setTool(toolName).build();
    }

}