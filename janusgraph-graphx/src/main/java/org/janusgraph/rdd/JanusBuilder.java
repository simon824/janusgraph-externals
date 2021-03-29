package org.janusgraph.rdd;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.janusgraph.core.JanusEdge;
import org.janusgraph.core.JanusRDDConfig;
import org.janusgraph.core.JanusRelationParser;
import org.janusgraph.core.JanusVertex;
import org.janusgraph.iterators.JanusEdgesIterator;
import org.janusgraph.iterators.JanusVerticesIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.reflect.ClassManifestFactory;

import java.util.Iterator;

/**
 * Created by zhangshiming
 */
public class JanusBuilder implements JanusRDDConfig {
    private static final Logger log = LoggerFactory.getLogger(JanusBuilder.class);

    private String regionDirectoriesPath;
    private String janusConfigPath;
    private String logConfigPath;

    private Function<JanusEdge, Boolean> filterEdge = e -> true;
    private Function<JanusVertex, Boolean> parseVertexProperties = v -> true;
    private Function2<JanusVertex, String, Boolean> parseVertexProperty = (v, property) -> true;
    private Function<JanusVertex, Boolean> parseInEdges = v -> true;
    private Function2<JanusVertex, String, Boolean> parseInEdge = (v, property) -> parseInEdges.call(v);
    private Function<JanusVertex, Boolean> parseOutEdges = v -> true;
    private Function2<JanusVertex, String, Boolean> parseOutEdge = (v, property) -> parseOutEdges.call(v);
    private Function<JanusEdge, Boolean> parseEdgeProperties = e -> true;
    private Function2<JanusEdge, String, Boolean> parseEdgeProperty = (e, property) -> true;

    public JanusBuilder(String myConfigPath) throws ConfigurationException {
        PropertiesConfiguration janusConfig = new PropertiesConfiguration(myConfigPath);

        regionDirectoriesPath(janusConfig.getString("region-directories-path"));
        janusConfigPath(janusConfig.getString("janus-config-path"));
        logConfigPath(janusConfig.getString("log-config-path"));
    }

    public JanusBuilder() {

    }

    public JanusRDD<JanusEdge> edgesRDD(SparkContext sc) {
        return new JanusRDD<JanusEdge>(sc, this, ClassManifestFactory.classType(JanusEdge.class)) {
            @Override
            public scala.collection.Iterator<JanusEdge> createRegionIterator(Iterator<JanusRelationParser> relationsIterator) {
                return new JanusEdgesIterator(relationsIterator, this.config);
            }
        };
    }

    public JanusRDD<JanusVertex> verticesRDD(SparkContext sc) {
        return new JanusRDD<JanusVertex>(sc, this, ClassManifestFactory.classType(JanusVertex.class)) {
            @Override
            public scala.collection.Iterator<JanusVertex> createRegionIterator(Iterator<JanusRelationParser> relationsIterator) {
                return new JanusVerticesIterator(relationsIterator, this.config);
            }
        };
    }


    public JanusBuilder regionDirectoriesPath(String path) {
        this.regionDirectoriesPath = path;

        return this;
    }

    public JanusBuilder janusConfigPath(String path) {
        this.janusConfigPath = path;

        return this;
    }

    public JanusBuilder logConfigPath(String path) {
        this.logConfigPath = path;

        return this;
    }

    public JanusBuilder filterEdge(Function<JanusEdge, Boolean> predicate) {
        this.filterEdge = predicate;

        return this;
    }

    public JanusBuilder parseVertexProperties(Function<JanusVertex, Boolean> predicate) {
        this.parseVertexProperties = predicate;

        return this;
    }

    public JanusBuilder parseVertexProperty(Function2<JanusVertex, String, Boolean> predicate) {
        this.parseVertexProperty = predicate;

        return this;
    }

    public JanusBuilder parseEdgeProperties(Function<JanusEdge, Boolean> predicate) {
        this.parseEdgeProperties = predicate;

        return this;
    }

    public JanusBuilder parseEdgeProperty(Function2<JanusEdge, String, Boolean> predicate) {
        this.parseEdgeProperty = predicate;

        return this;
    }

    public JanusBuilder parseInEdges(Function<JanusVertex, Boolean> predicate) {
        this.parseInEdges = predicate;

        return this;
    }

    public JanusBuilder parseInEdge(Function2<JanusVertex, String, Boolean> predicate) {
        this.parseInEdge = predicate;

        return this;
    }

    public JanusBuilder parseOutEdges(Function<JanusVertex, Boolean> predicate) {
        this.parseOutEdges = predicate;

        return this;
    }

    public JanusBuilder parseOutEdge(Function2<JanusVertex, String, Boolean> predicate) {
        this.parseOutEdge = predicate;

        return this;
    }

    /**
     * Getters
     */

    @Override
    public String regionDirectoriesPath() {
        return regionDirectoriesPath;
    }

    @Override
    public String janusConfigPath() {
        return janusConfigPath;
    }

    @Override
    public String logConfigPath() {
        return logConfigPath;
    }

    @Override
    public Function<JanusVertex, Boolean> parseVertexProperties() {
        return parseVertexProperties;
    }

    @Override
    public Function2<JanusVertex, String, Boolean> parseVertexProperty() {
        return parseVertexProperty;
    }

    @Override
    public Function<JanusVertex, Boolean> parseInEdges() {
        return parseInEdges;
    }

    @Override
    public Function2<JanusVertex, String, Boolean> parseInEdge() {
        return parseInEdge;
    }

    @Override
    public Function<JanusVertex, Boolean> parseOutEdges() {
        return parseOutEdges;
    }

    @Override
    public Function2<JanusVertex, String, Boolean> parseOutEdge() {
        return parseOutEdge;
    }

    @Override
    public Function<JanusEdge, Boolean> parseEdgeProperties() {
        return parseEdgeProperties;
    }

    @Override
    public Function2<JanusEdge, String, Boolean> parseEdgeProperty() {
        return parseEdgeProperty;
    }

    @Override
    public Function<JanusEdge, Boolean> filterEdge() {
        return filterEdge;
    }

    public String version() {
        return "Mizo3.1.4";
    }
}
