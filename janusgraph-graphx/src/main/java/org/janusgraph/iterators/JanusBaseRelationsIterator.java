package org.janusgraph.iterators;


import org.janusgraph.core.JanusRDDConfig;
import org.janusgraph.core.JanusRelationParser;
import org.janusgraph.core.JanusEdge;
import org.janusgraph.core.JanusVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.AbstractIterator;

import java.util.Iterator;

/**
 * Created by zhangshiming
 */
public abstract class JanusBaseRelationsIterator<TReturn> extends AbstractIterator<TReturn> implements Iterator<TReturn> {
    private static final Logger log = LoggerFactory.getLogger(JanusBaseRelationsIterator.class);

    protected JanusRDDConfig config;

    protected long verticesCounter = 0;

    public JanusBaseRelationsIterator(JanusRDDConfig config) {
        this.config = config;
    }

    public JanusEdge getEdgeOrNull(JanusRelationParser relation, JanusVertex vertexToUpdate) {
        if (!relation.isSystemType() && relation.isKnownType()) {
            if (relation.isProperty()) {
                handleProperty(relation, vertexToUpdate);
            } else if (relation.isOutEdge()) {
                return handleEdge(relation, vertexToUpdate, true);
            } else if (relation.isInEdge()) {
                return handleEdge(relation, vertexToUpdate, false);
            }
        }

        return null;
    }

    protected JanusVertex setupNewVertex(JanusRelationParser relation) {
        if (++verticesCounter % 50000 == 0) {
            log.info("VERTICES counter: " + verticesCounter);
        }

        return new JanusVertex(relation.getVertexId());
    }

    protected JanusEdge handleEdge(JanusRelationParser relation, JanusVertex vertexToUpdate, boolean isOutEdge) {
        if (shouldParseEdge(vertexToUpdate, relation.getTypeName(), isOutEdge)) {
            JanusEdge newEdge = new JanusEdge(relation.getTypeName(), relation.getTypeId(),
                    relation.getRelationId(), isOutEdge, vertexToUpdate, relation.getOtherVertexId());

            if (shouldParseEdgeProperties(newEdge) && relation.valueHasRemaining()) {
                parseEdgeProperties(relation, newEdge);
            }

            if (shouldFilterEdge(newEdge))  {
                return newEdge;
            }
        }

        return null;
    }

    protected void handleProperty(JanusRelationParser relation, JanusVertex vertexToUpdate) {
        if (shouldParseVertexProperty(vertexToUpdate, relation.getTypeName())) {
            vertexToUpdate.initializedProperties().put(relation.getTypeName(), relation.readPropertyValue());
        }
    }

    protected void parseEdgeProperties(JanusRelationParser relation, JanusEdge edgeToUpdate) {
        while (relation.valueHasRemaining()) {
            String edgePropertyName = relation.readPropertyName();

            if (edgePropertyName != null && shouldParseEdgeProperty(edgeToUpdate, edgePropertyName)) {
                edgeToUpdate.initializedProperties().put(edgePropertyName, relation.readPropertyValue());
            } else {
                relation.readPropertyValue();
            }
        }
    }

    protected boolean shouldParseVertexProperty(JanusVertex vertex, String propertyName) {
        try {
            return this.config.parseVertexProperties().call(vertex) &&
                    this.config.parseVertexProperty().call(vertex, propertyName);
        } catch (Exception e) {
            log.warn("Vertex predicate invocation failed due to exception (vertex-id: {}, property-name: {}, exception: {})", vertex.id(), propertyName, e);
        }

        return true;
    }

    protected boolean shouldParseEdge(JanusVertex vertex, String edgeLabel, boolean isOutEdge) {
        try {
            return (isOutEdge ? this.config.parseOutEdges() : this.config.parseInEdges()).call(vertex) &&
                    (isOutEdge ? this.config.parseOutEdge() : this.config.parseInEdge()).call(vertex, edgeLabel);
        } catch (Exception e) {
            log.warn("Vertex predicate invocation failed due to exception (vertex-id: {}, out-edge: {}, edge-label: {}, exception: {})", vertex.id(), isOutEdge, edgeLabel, e);
        }

        return true;
    }

    protected boolean shouldParseEdgeProperties(JanusEdge edge) {
        try {
            return this.config.parseEdgeProperties().call(edge);
        } catch (Exception e) {
            log.warn("Vertex predicate invocation failed due to exception (vertex-id: {}, relation-id: {}, edge-label: {}, exception: {})", edge.vertex().id(), edge.relationId(), edge.label(), e);
        }

        return true;
    }

    protected boolean shouldParseEdgeProperty(JanusEdge edge, String propertyName) {
        try {
            return this.config.parseEdgeProperty().call(edge, propertyName);
        } catch (Exception e) {
            log.warn("Vertex predicate invocation failed due to exception (vertex-id: {}, property-name: {}, relation-id: {}, edge-label: {}, exception: {})", edge.vertex().id(), propertyName, edge.relationId(), edge.label(), e);
        }

        return true;
    }

    protected boolean shouldFilterEdge(JanusEdge edge) {
        try {
            return this.config.filterEdge().call(edge);
        } catch (Exception e) {
            log.warn("Vertex predicate invocation failed due to exception (vertex-id: {}, relation-id: {}, edge-label: {}, exception: {})", edge.vertex().id(), edge.relationId(), edge.label(), e);
        }

        return true;
    }
}
