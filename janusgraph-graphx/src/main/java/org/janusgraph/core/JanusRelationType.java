package org.janusgraph.core;

import com.google.common.base.Predicate;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.*;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.SchemaStatus;
import org.janusgraph.diskstorage.EntryList;
import org.janusgraph.diskstorage.keycolumnvalue.SliceQuery;
import org.janusgraph.graphdb.internal.InternalRelation;
import org.janusgraph.graphdb.internal.InternalRelationType;
import org.janusgraph.graphdb.internal.InternalVertex;
import org.janusgraph.graphdb.internal.Order;
import org.janusgraph.graphdb.query.vertex.VertexCentricQueryBuilder;
import org.janusgraph.graphdb.transaction.StandardJanusGraphTx;
import org.janusgraph.graphdb.types.IndexType;
import org.janusgraph.util.datastructures.Retriever;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangshiming
 */
public class JanusRelationType implements Serializable, InternalRelationType, PropertyKey {

    private final SchemaStatus status;
    private final long[] sortKey;
    private final String name;
    private final long id;
    private final boolean isInvisibleType;
    private final long[] signature;
    private final Order sortOrder;
    private final Multiplicity multiplicity;
    private final ConsistencyModifier consistencyModifier;
    private final Integer ttl;
    private final boolean isPropertyKey;
    private final boolean isEdgeLabel;
    private final Class<?> dataType;
    private final Cardinality cardinality;

    public JanusRelationType(InternalRelationType relationType) {
        status = relationType.getStatus();
        sortKey = relationType.getSortKey();
        name = relationType.name();
        id = relationType.longId();
        isInvisibleType = relationType.isInvisibleType();
        signature = relationType.getSignature();
        sortOrder = relationType.getSortOrder();
        multiplicity = relationType.multiplicity();
        consistencyModifier = relationType.getConsistencyModifier();
        ttl = relationType.getTTL();
        isPropertyKey = relationType.isPropertyKey();
        isEdgeLabel = relationType.isEdgeLabel();

        if (relationType instanceof PropertyKey) {
            dataType = ((PropertyKey)relationType).dataType();
            cardinality = ((PropertyKey)relationType).cardinality();
        } else {
            dataType = null;
            cardinality = null;
        }
    }

    @Override
    public boolean isInvisibleType() {
        return isInvisibleType;
    }

    @Override
    public long[] getSignature() {
        return signature;
    }

    @Override
    public long[] getSortKey() {
        return sortKey;
    }

    @Override
    public Order getSortOrder() {
        return sortOrder;
    }

    @Override
    public Multiplicity multiplicity() {
        return multiplicity;
    }

    @Override
    public ConsistencyModifier getConsistencyModifier() {
        return consistencyModifier;
    }

    @Override
    public Integer getTTL() {
        return ttl;
    }

    @Override
    public boolean isUnidirected(Direction direction) {
        return false;
    }

    @Override
    public InternalRelationType getBaseType() {
        return null;
    }

    @Override
    public Iterable<InternalRelationType> getRelationIndexes() {
        return null;
    }

    @Override
    public SchemaStatus getStatus() {
        return status;
    }

    @Override
    public Iterable<IndexType> getKeyIndexes() {
        return null;
    }

    @Override
    public boolean isPropertyKey() {
        return isPropertyKey;
    }

    @Override
    public boolean isEdgeLabel() {
        return isEdgeLabel;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public InternalVertex it() {
        return null;
    }

    @Override
    public StandardJanusGraphTx tx() {
        return null;
    }

    @Override
    public void setId(long l) {

    }

    @Override
    public byte getLifeCycle() {
        return 0;
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void removeRelation(InternalRelation internalRelation) {

    }

    @Override
    public boolean addRelation(InternalRelation internalRelation) {
        return false;
    }

    @Override
    public List<InternalRelation> getAddedRelations(Predicate<InternalRelation> predicate) {
        return null;
    }

    @Override
    public EntryList loadRelations(SliceQuery sliceQuery, Retriever<SliceQuery, EntryList> retriever) {
        return null;
    }

    @Override
    public boolean hasLoadedRelations(SliceQuery sliceQuery) {
        return false;
    }

    @Override
    public boolean hasRemovedRelations() {
        return false;
    }

    @Override
    public boolean hasAddedRelations() {
        return false;
    }

    @Override
    public JanusGraphEdge addEdge(String s, Vertex vertex, Object... objects) {
        return null;
    }

    @Override
    public <V> JanusGraphVertexProperty<V> property(String s, V v, Object... objects) {
        return null;
    }

    @Override
    public <V> JanusGraphVertexProperty<V> property(VertexProperty.Cardinality cardinality, String s, V v, Object... objects) {
        return null;
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... strings) {
        return null;
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... strings) {
        return null;
    }

    @Override
    public VertexLabel vertexLabel() {
        return null;
    }

    @Override
    public VertexCentricQueryBuilder query() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public long longId() {
        return id;
    }

    @Override
    public boolean hasId() {
        return false;
    }

    @Override
    public void remove() {

    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... strings) {
        return null;
    }

    @Override
    public <V> V valueOrNull(PropertyKey propertyKey) {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public boolean isRemoved() {
        return false;
    }

    public Class<?> dataType() {
        return dataType;
    }

    @Override
    public Cardinality cardinality() {
        return cardinality;
    }
}
