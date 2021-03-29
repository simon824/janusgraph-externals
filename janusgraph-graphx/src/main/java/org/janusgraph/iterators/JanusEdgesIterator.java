package org.janusgraph.iterators;

import org.janusgraph.core.JanusRDDConfig;
import org.janusgraph.core.JanusRelationParser;
import org.janusgraph.core.JanusEdge;
import org.janusgraph.core.JanusVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by zhangshiming
 */
public class JanusEdgesIterator extends JanusBaseRelationsIterator<JanusEdge> {
    private static final Logger log = LoggerFactory.getLogger(JanusEdgesIterator.class);

    protected JanusEdge edgeToReturn;

    protected Iterator<JanusRelationParser> relationsIterator;

    private JanusVertex currentVertex = null;

    private long lastVertexId = 0;

    public JanusEdgesIterator(Iterator<JanusRelationParser> relationsIterator, JanusRDDConfig config) {
        super(config);

        this.relationsIterator = relationsIterator;
    }

    @Override
    public boolean hasNext() {
        if (edgeToReturn != null) {
            return true;
        }

        edgeToReturn = null;

        while (edgeToReturn == null && this.relationsIterator.hasNext()) {
            JanusRelationParser relation = this.relationsIterator.next();

            if (lastVertexId != relation.getVertexId()) {
                currentVertex = setupNewVertex(relation);
                lastVertexId = relation.getVertexId();
            }

            edgeToReturn = getEdgeOrNull(relation, currentVertex);
        }

        return edgeToReturn != null;
    }

    @Override
    public JanusEdge next() {
        JanusEdge toReturn = edgeToReturn;
        edgeToReturn = null;

        return toReturn;
    }
}
