package org.janusgraph;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.janusgraph.core.JanusEdge;
import org.janusgraph.rdd.JanusBuilder;

import java.util.List;

/**
 * Created by zhangshiming
 */
public class JanusEdgesCounterExample {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf()
                .setAppName("Janus")
                .setMaster("local[*]");
        SparkContext sc = new SparkContext(conf);

        JavaRDD<JanusEdge> janusEdgeJavaRDD = new JanusBuilder()
                //                .logConfigPath("log4j.properties")
                .janusConfigPath("./janus.properties")
                .regionDirectoriesPath("hdfs://xxxx:9000/hbase/data/default/janustest/*/e")
                .parseInEdges(v -> false)
                .edgesRDD(sc).toJavaRDD();

        List<JanusEdge> collect = janusEdgeJavaRDD.collect();

        for (JanusEdge janusVertex : collect) {
            System.out.println(" #############: " + janusVertex.toString());
        }

        sc.stop();

    }
}
