package org.janusgraph.core;

/**
 * Created by zhangshiming
 */
public interface JanusRelationParser {
    long getTypeId();

    String getTypeName();

    long getRelationId();

    long getVertexId();

    long getOtherVertexId();

    Object readPropertyValue();

    String readPropertyName();

    boolean valueHasRemaining();

    boolean isSystemType();

    boolean isKnownType();

    boolean isProperty();

    boolean isOutEdge();

    boolean isInEdge();

}
