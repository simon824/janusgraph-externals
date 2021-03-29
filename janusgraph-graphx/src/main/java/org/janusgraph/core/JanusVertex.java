package org.janusgraph.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangshiming.
 */
public class JanusVertex implements Serializable {
    private long id;
    private Map<String, Object> properties;
    private List<JanusEdge> inEdges;
    private List<JanusEdge> outEdges;

    public JanusVertex(long id) {

        this.id = id;
    }

    public Map<String, Object> initializedProperties() {
        if (properties == null) {
            properties = Maps.newHashMap();
        }

        return properties;
    }

    public List<JanusEdge> initializedInEdges() {
        if (inEdges == null) {
            inEdges = Lists.newArrayList();
        }

        return inEdges;
    }

    public List<JanusEdge> initializedOutEdges() {
        if (outEdges == null) {
            outEdges = Lists.newArrayList();
        }

        return outEdges;
    }

    public long id() {
        return id;
    }

    public Map<String, Object> properties() {
        return properties;
    }

    public List<JanusEdge> inEdges() {
        return inEdges;
    }

    public List<JanusEdge> outEdges() {
        return outEdges;
    }


    @Override
    public String toString() {
        return "JanusVertex{" +
                "id=" + id +
                ", properties=" + properties +
                ", inEdges=" + inEdges +
                ", outEdges=" + outEdges +
                '}';
    }
}
