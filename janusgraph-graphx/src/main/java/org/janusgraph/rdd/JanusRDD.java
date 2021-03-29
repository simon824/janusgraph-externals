package org.janusgraph.rdd;

import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.Partition;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.rdd.RDD;
import org.janusgraph.core.*;
import org.janusgraph.core.attribute.Contain;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.internal.InternalRelationType;
import org.janusgraph.graphdb.internal.JanusGraphSchemaCategory;
import org.janusgraph.graphdb.transaction.StandardJanusGraphTx;
import org.janusgraph.graphdb.types.system.BaseKey;
import org.janusgraph.hbase.JanusHBaseRelationParser;
import org.janusgraph.hbase.JanusRegionFamilyCellsIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.mutable.ArrayBuffer;
import scala.reflect.ClassTag;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by zhangshiming
 */
public abstract class JanusRDD<TReturn> extends RDD<TReturn> implements Serializable {
    public static final Logger log = LoggerFactory.getLogger(JanusRDD.class);

    protected List<String> regionsPaths;

    protected Map<Long, JanusRelationType> relationTypes;

    protected JanusRDDConfig config;

    public JanusRDD(SparkContext context, JanusRDDConfig config, ClassTag<TReturn> classTag) {
        super(context, new ArrayBuffer<>(), classTag);

        if (!Strings.isNullOrEmpty(config.logConfigPath())) {
            PropertyConfigurator.configure(config.logConfigPath());
        }

        this.config = config;
        this.regionsPaths = getRegionsPaths(config.regionDirectoriesPath());
        this.relationTypes = loadRelationTypes(config.janusConfigPath());
    }

    @Override
    public scala.collection.Iterator<TReturn> compute(Partition split, TaskContext context) {
        String regionEdgesFamilyPath = this.regionsPaths.get(split.index());
        log.info("Running Mizo on region #{} located at: {}", split.index(), regionEdgesFamilyPath);

        return createRegionIterator(createRegionRelationsIterator(regionEdgesFamilyPath));
    }

    protected Iterator<JanusRelationParser> createRegionRelationsIterator(String regionEdgesFamilyPath) {
        try {
            JanusRegionFamilyCellsIterator janusRegionFamilyCellsIterator = new JanusRegionFamilyCellsIterator(regionEdgesFamilyPath);

            Iterator<JanusRelationParser> transform = Iterators.transform(
                    janusRegionFamilyCellsIterator, // PeekingIterator<Cell>
                    cell -> new JanusHBaseRelationParser(this.relationTypes, cell)
            );

            return transform;
        } catch (IOException e) {
            log.error("Failed to initialized region relations reader due to inner exception: {}", e);

            return Collections.emptyIterator();
        }
    }

    public abstract scala.collection.Iterator<TReturn> createRegionIterator(Iterator<JanusRelationParser> relationIterator);


    @Override
    public Partition[] getPartitions() {
        return Iterators.toArray(IntStream
                .range(0, this.regionsPaths.size())
                .mapToObj(i -> (Partition) () -> i)
                .iterator(), Partition.class);
    }

    protected static List<String> getRegionsPaths(String regionDirectoryPaths) {
        try {
            org.apache.hadoop.fs.Path regionDirectory = new org.apache.hadoop.fs.Path(regionDirectoryPaths);
            FileSystem fs = regionDirectory.getFileSystem(new Configuration());

            return Arrays.stream(fs.globStatus(regionDirectory, new FSUtils.RegionDirFilter(fs)))
                    .map(file -> file.getPath().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to get partitions due to inner exception: {}", e);

            return Collections.emptyList();
        }
    }


    protected static HashMap<Long, JanusRelationType> loadRelationTypes(String ConfigPath) {

        BaseConfiguration baseConfiguration = new BaseConfiguration();

        baseConfiguration.setProperty("cache.db-cache-size", "0.5");
        baseConfiguration.setProperty("cache.db-cache-time", "180000");
        baseConfiguration.setProperty("cache.db-cache-clean-wait", "20");
        baseConfiguration.setProperty("cache.db-cache", "true");
        baseConfiguration.setProperty("storage.port", "2181");

        baseConfiguration.setProperty("storage.hostname", "xxx,xxx,xxx");
        baseConfiguration.setProperty("storage.hbase.table", "janustest");
        baseConfiguration.setProperty("storage.backend", "hbase");
        baseConfiguration.setProperty("gremlin.graph", "org.janusgraph.core.JanusGraphFactory");

        JanusGraph g = JanusGraphFactory.open(baseConfiguration);

        StandardJanusGraphTx tx = (StandardJanusGraphTx) g.buildTransaction().readOnly().start();

        HashMap<Long, JanusRelationType> relations = Maps.newHashMap();

        tx.query()
                .has(BaseKey.SchemaCategory, Contain.IN, Lists.newArrayList(JanusGraphSchemaCategory.values()))
                .vertices()
                .forEach(v -> {
                    if (v instanceof InternalRelationType)
                        relations.put(v.longId(), new JanusRelationType((InternalRelationType) v));
                });
        tx.commit();
        tx.close();
        g.close();

        try {
            ((StandardJanusGraph) g).getBackend().close();
        } catch (BackendException e) {
            e.printStackTrace();
        }

        return relations;
    }


}
