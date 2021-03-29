package org.janusgraph.hbase;

import com.carrotsearch.hppc.cursors.LongObjectCursor;
import org.janusgraph.core.JanusRelationParser;
import org.janusgraph.core.JanusRelationType;
import org.janusgraph.hbase.patches.JanusReadArrayBuffer;
import org.apache.hadoop.hbase.Cell;
import org.janusgraph.core.RelationType;
import org.janusgraph.core.VertexLabel;
import org.janusgraph.diskstorage.Entry;
import org.janusgraph.diskstorage.ReadBuffer;
import org.janusgraph.diskstorage.util.StaticArrayBuffer;
import org.janusgraph.diskstorage.util.StaticArrayEntry;
import org.janusgraph.graphdb.database.EdgeSerializer;
import org.janusgraph.graphdb.database.idhandling.IDHandler;
import org.janusgraph.graphdb.database.serialize.StandardSerializer;
import org.janusgraph.graphdb.idmanagement.IDManager;
import org.janusgraph.graphdb.relations.RelationCache;
import org.janusgraph.graphdb.types.TypeInspector;
import org.janusgraph.graphdb.types.system.BaseKey;
import org.janusgraph.graphdb.types.system.BaseLabel;
import org.janusgraph.util.stats.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhangshiming
 */

public class JanusHBaseRelationParser implements JanusRelationParser {
    private static final Logger log =
            LoggerFactory.getLogger(JanusHBaseRelationParser.class);
    private Iterator<LongObjectCursor<Object>> propertiesIterator;

    private Object nextPropertyValue;

    private Object propertyValue;

    private JanusRelationType relationType;

    private final long vertexId;

    private long typeId;

    private IDHandler.DirectionID directionID;

    private long relationId;

    private long otherVertexId;

    private final static IDManager ID_MANAGER = new IDManager(NumberUtil.getPowerOf2(32));

    private final Map<Long, JanusRelationType> relationTypes;

    private EdgeSerializer JANUS_EDGE_SERIALIZER = new org.janusgraph.graphdb.database.EdgeSerializer(new StandardSerializer());


    private TypeInspector JANUS_TYPE_INSPECTOR = new TypeInspector() {
        @Override
        public RelationType getExistingRelationType(long id) {
            return relationTypes.get(id);
        }

        @Override
        public VertexLabel getExistingVertexLabel(long l) {
            return null;
        }

        @Override
        public boolean containsRelationType(String s) {
            return false;
        }

        @Override
        public RelationType getRelationType(String s) {
            return null;
        }
    };

    public JanusHBaseRelationParser(Map<Long, JanusRelationType> relationTypes, Cell cell) {
        this.relationTypes = relationTypes;
        this.vertexId = ID_MANAGER.getKeyID(
                new StaticArrayBuffer(cell.getRowArray(), cell.getRowOffset(), cell.getRowOffset() + cell.getRowLength()));
        System.out.println(vertexId);
        Entry entry = StaticArrayEntry.of(
                new JanusReadArrayBuffer(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierOffset() + cell.getQualifierLength()),
                new JanusReadArrayBuffer(cell.getValueArray(), cell.getValueOffset(), cell.getValueOffset() + cell.getValueLength())
        );

        IDHandler.RelationTypeParse typeAndDirection = IDHandler.readRelationType(entry.asReadBuffer());
        typeId = typeAndDirection.typeId;
        directionID = typeAndDirection.dirID;
        relationType = this.relationTypes.get(this.typeId);


        if (!isSystemType()) {
            RelationCache relationCache = JANUS_EDGE_SERIALIZER.parseRelation(entry, false, JANUS_TYPE_INSPECTOR);

            if (isProperty()) {
                propertyValue = relationCache.getValue();
            } else {
                relationId = relationCache.relationId;
                otherVertexId = relationCache.getOtherVertexId();
                propertiesIterator = relationCache.propertyIterator();
            }

            if (!isKnownType()) {
                log.warn("Unknown relation type (vertex-id={}, type-id={})", this.vertexId, this.typeId);
            }
        }
    }

    private Entry createEntry(Cell cell) {
        return StaticArrayEntry.of(new JanusReadArrayBuffer(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierOffset() + cell.getQualifierLength()),
                new JanusReadArrayBuffer(cell.getValueArray(), cell.getValueOffset(), cell.getValueOffset() + cell.getValueLength()));
    }

    private void extractRelationMetadata(ReadBuffer buffer) {
        IDHandler.RelationTypeParse typeAndDirection = IDHandler.readRelationType(buffer);
        typeId = typeAndDirection.typeId;
        directionID = typeAndDirection.dirID;
        relationType = this.relationTypes.get(this.typeId);
    }

    private static long parseVertexId(byte[] rowArray, int rowOffset, int rowLength) {
        return ID_MANAGER.getKeyID(new StaticArrayBuffer(rowArray, rowOffset, rowOffset + rowLength));
    }

    @Override
    public Object readPropertyValue() {
        if (isProperty()) {
            return propertyValue;
        }

        return nextPropertyValue;
    }

    @Override
    public String readPropertyName() {
        if (isProperty()) {
            return relationType.name();
        }

        LongObjectCursor<Object> next = propertiesIterator.next();
        nextPropertyValue = next.value;
        return relationTypes.get(next.key).name();

    }

    @Override
    public boolean valueHasRemaining() {
        return propertiesIterator.hasNext();
    }


    @Override

    public boolean isSystemType() {
        return IDManager.isSystemRelationTypeId(typeId) ||
                typeId == BaseKey.VertexExists.longId() ||
                typeId == BaseLabel.VertexLabelEdge.longId() ||
                typeId == BaseKey.SchemaCategory.longId() ||
                typeId == BaseKey.SchemaDefinitionProperty.longId() ||
                typeId == BaseKey.SchemaDefinitionDesc.longId() ||
                typeId == BaseKey.SchemaName.longId() ||
                typeId == BaseLabel.SchemaDefinitionEdge.longId();
    }


    @Override
    public boolean isKnownType() {
        return this.relationType != null;
    }

    @Override
    public long getTypeId() {
        return typeId;
    }

    @Override
    public String getTypeName() {
        return this.relationType.name();
    }

    @Override
    public long getRelationId() {
        return relationId;
    }

    @Override
    public long getVertexId() {
        return vertexId;
    }

    @Override
    public long getOtherVertexId() {
        return otherVertexId;
    }

    @Override
    public boolean isProperty() {
        return directionID.equals(IDHandler.DirectionID.PROPERTY_DIR);
    }

    @Override
    public boolean isOutEdge() {
        return directionID.equals(IDHandler.DirectionID.EDGE_OUT_DIR);
    }

    @Override
    public boolean isInEdge() {
        return directionID.equals(IDHandler.DirectionID.EDGE_IN_DIR);
    }
}

