package org.janusgraph.hbase.patches;

import org.janusgraph.diskstorage.util.StaticArrayBuffer;

/**
 * Created by zhangshiming
 */
public class JanusReadArrayBuffer extends StaticArrayBuffer {
    public JanusReadArrayBuffer(byte[] array, int offset, int limit) {
        super(array, offset, limit);
    }
}
