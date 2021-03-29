package org.janusgraph.iterators;

import org.janusgraph.core.JanusRDDConfig;
import org.janusgraph.core.JanusRelationParser;
import org.janusgraph.core.JanusEdge;
import org.janusgraph.core.JanusVertex;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by zhangshiming .
 */
public class JanusVerticesIterator extends JanusBaseRelationsIterator<JanusVertex> {
    private static final Logger log = LoggerFactory.getLogger(JanusVerticesIterator.class);

    protected PeekingIterator<JanusRelationParser> relationsIterator;

    public JanusVerticesIterator(Iterator<JanusRelationParser> relationsIterator, JanusRDDConfig config) {
        super(config);

        this.relationsIterator = Iterators.peekingIterator(relationsIterator);
    }

    @Override
    public boolean hasNext() {
        return relationsIterator.hasNext();
    }

    @Override
    public JanusVertex next() {
        long lastVertexId = this.relationsIterator.peek().getVertexId();
        JanusVertex currentVertex = setupNewVertex(this.relationsIterator.peek());

        while (this.relationsIterator.hasNext() && lastVertexId == this.relationsIterator.peek().getVertexId()) {
            JanusEdge edgeOrNull = getEdgeOrNull(this.relationsIterator.next(), currentVertex);

            if (edgeOrNull != null) {
                if (edgeOrNull.isOutEdge()) {
                    currentVertex.initializedOutEdges().add(edgeOrNull);
                } else {
                    currentVertex.initializedInEdges().add(edgeOrNull);
                }
            }
        }

        return currentVertex;
    }
}
