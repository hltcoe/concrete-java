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
import edu.jhu.hlt.concrete.Graph;
import edu.jhu.hlt.concrete.ConcreteException;

public class AttribUtil {

    private static final Descriptor VERTEX_KIND_ATTRIBUTE = Graph.VertexKindAttribute.getDescriptor();
    private static final Descriptor MENTION_ATTRIBUTE = Graph.MentionAttribute.getDescriptor();
    private static final Descriptor BOOLEAN_ATTRIBUTE = Graph.BooleanAttribute.getDescriptor();
    private static final Descriptor STRING_ATTRIBUTE = Graph.StringAttribute.getDescriptor();
    private static final Descriptor FLOAT_ATTRIBUTE = Graph.FloatAttribute.getDescriptor();
    private static final Descriptor INT32_ATTRIBUTE = Graph.Int32Attribute.getDescriptor();
    private static final Descriptor INT64_ATTRIBUTE = Graph.Int64Attribute.getDescriptor();
    private static final Descriptor COMMUNICATION_GUID_ATTRIBUTE = Graph.CommunicationGUIDAttribute.getDescriptor();
    private static final Descriptor STRING_FLOAT_MAP_ATTRIBUTE = Graph.StringFloatMapAttribute.getDescriptor();

    // private static final Descriptor Xx_ATTRIBUTE =
    // Concrete.XxAttribute.getDescriptor();

    public static Message buildAttribute(FieldDescriptor field, Object value, String toolName) throws ConcreteException {
        return buildAttribute(field, value, buildMetadata(toolName));
    }

    public static Message buildAttribute(FieldDescriptor field, Object value, String toolName, float confidence) throws ConcreteException {
        return buildAttribute(field, value, buildMetadata(toolName, confidence));
    }

    public static Message buildAttribute(FieldDescriptor field, Object value, Concrete.AnnotationMetadata metadata) 
            throws ConcreteException {
        if (field == null)
            throw new ConcreteException("FieldDescriptor is NULL!  This probably means that you had a typo "
                    + "in a call to FieldDescriptor.findFieldByName.");
        Descriptor attribDescriptor = field.getMessageType();
        if (attribDescriptor == MENTION_ATTRIBUTE) {
            return Graph.MentionAttribute.newBuilder().setMetadata(metadata).setValue((Concrete.EntityMentionRef) value)
                    .setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == BOOLEAN_ATTRIBUTE) {
            return Graph.BooleanAttribute.newBuilder().setMetadata(metadata).setValue((Boolean) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == STRING_ATTRIBUTE) {
            return Graph.StringAttribute.newBuilder().setMetadata(metadata).setValue((String) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == FLOAT_ATTRIBUTE) {
            return Graph.FloatAttribute.newBuilder().setMetadata(metadata).setValue((Float) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == INT32_ATTRIBUTE) {
            return Graph.Int32Attribute.newBuilder().setMetadata(metadata).setValue((Integer) value).setUuid(IdUtil.generateUUID())
                    .build();
        } else if (attribDescriptor == INT64_ATTRIBUTE) {
            return Graph.Int64Attribute.newBuilder().setMetadata(metadata).setValue((Long) value).setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == COMMUNICATION_GUID_ATTRIBUTE) {
            return Graph.CommunicationGUIDAttribute.newBuilder().setMetadata(metadata).setValue((Concrete.CommunicationGUID) value)
                    .setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == STRING_FLOAT_MAP_ATTRIBUTE) {
            return Graph.StringFloatMapAttribute.newBuilder().setMetadata(metadata).addAllValue(buildStringFloatMap(value))
                    .setUuid(IdUtil.generateUUID()).build();
        } else if (attribDescriptor == VERTEX_KIND_ATTRIBUTE) {
            return Graph.VertexKindAttribute.newBuilder().setMetadata(metadata).setValue((Graph.Vertex.Kind) value)
                    .setUuid(IdUtil.generateUUID()).build();
        } else {
            throw new ConcreteException("Unknown attribute type");
        }
    }

    private static List<Graph.StringFloatMapAttribute.Entry> buildStringFloatMap(Object object) throws ConcreteException {
        try {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) object;
            List<Graph.StringFloatMapAttribute.Entry> result = new ArrayList<Graph.StringFloatMapAttribute.Entry>(map.size());
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                result.add(Graph.StringFloatMapAttribute.Entry.newBuilder().setKey((String) entry.getKey())
                        .setValue((Float) entry.getValue()).build());
            }
            return result;
        } catch (ClassCastException e) {
            throw new ConcreteException("Expected a Map<String,Float>!");
        }
    }

    public static Concrete.AnnotationMetadata buildMetadata(String toolName, float confidence) {
        return Concrete.AnnotationMetadata.newBuilder().setTool(toolName).setConfidence(confidence).build();
    }

    public static Concrete.AnnotationMetadata buildMetadata(String toolName) {
        return Concrete.AnnotationMetadata.newBuilder().setTool(toolName).build();
    }

}