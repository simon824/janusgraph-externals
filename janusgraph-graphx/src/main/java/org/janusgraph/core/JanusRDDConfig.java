package org.janusgraph.core;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import java.io.Serializable;

/**
 * Created by zhangshiming
 */
public interface JanusRDDConfig extends Serializable {
    String regionDirectoriesPath();
    String janusConfigPath();
    String logConfigPath();

    Function<JanusVertex, Boolean> parseVertexProperties();
    Function2<JanusVertex, String, Boolean> parseVertexProperty();
    Function<JanusVertex, Boolean> parseInEdges();
    Function2<JanusVertex, String, Boolean> parseInEdge();
    Function<JanusVertex, Boolean> parseOutEdges();
    Function2<JanusVertex, String, Boolean> parseOutEdge();
    Function<JanusEdge, Boolean> parseEdgeProperties();
    Function2<JanusEdge, String, Boolean> parseEdgeProperty();
    Function<JanusEdge, Boolean> filterEdge();
}
