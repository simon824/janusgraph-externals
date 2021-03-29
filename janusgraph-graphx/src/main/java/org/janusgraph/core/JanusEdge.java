package org.janusgraph.core;

import com.google.common.collect.Maps;
import org.janusgraph.graphdb.relations.RelationIdentifier;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by zhangshiming.
 */
public class JanusEdge implements Serializable {
    private final String label;
    private final long relationId;
    private final JanusVertex vertex;
    private final boolean isOutEdge;
    private final long otherVertexId;
    private Map<String, Object> properties;
    private final long typeId;

    public JanusEdge(String label, long typeId, long relationId, boolean isOutEdge,
                     JanusVertex vertex, long otherVertexId) {

        this.label = label;
        this.typeId = typeId;
        this.relationId = relationId;
        this.isOutEdge = isOutEdge;
        this.vertex = vertex;
        this.otherVertexId = otherVertexId;
    }

    public String label() {
        return label;
    }

    public long relationId() {
        return relationId;
    }

    public JanusVertex vertex() {
        return vertex;
    }

    public boolean isOutEdge() {
        return isOutEdge;
    }

    public long getTypeId() {
        return typeId;
    }

    public long otherVertexId() {
        return otherVertexId;
    }

    public Map<String, Object> properties() {
        return properties;
    }

    public Map<String, Object> initializedProperties() {
        if (properties == null) {
            properties = Maps.newHashMap();
        }

        return properties;
    }

    public String janusId() {
        return RelationIdentifier.get(new long[]{
                relationId,
                isOutEdge ? vertex.id() : otherVertexId,
                typeId,
                isOutEdge ? otherVertexId : vertex.id()}).toString();
    }

    @Override
    public String toString() {
        return "JanusEdge{" +
                "label='" + label + '\'' +
                ", relationId=" + relationId +
                ", vertex=" + vertex +
                ", isOutEdge=" + isOutEdge +
                ", otherVertexId=" + otherVertexId +
                ", properties=" + properties +
                ", typeId=" + typeId +
                '}';
    }
}
