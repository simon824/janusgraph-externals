package org.janusgraph.hbase;

import com.google.common.collect.AbstractIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by zhangshiming
 */
public class JanusHFileIterator extends AbstractIterator<Cell> {
    private static final Logger log = LoggerFactory.getLogger(JanusHFileIterator.class);

    private final HFileScanner hfileScanner;

    public JanusHFileIterator(HFileScanner scanner) {
        this.hfileScanner = scanner;
    }

    @Override
    protected Cell computeNext() {
        try {
            if (this.hfileScanner.next()) {
                return this.hfileScanner.getCell();
            } else {
                return endOfData();
            }
        } catch (Exception e) {
            log.warn("Failed to get next cell from HFile due to exception (scanner: {}, exception: {})",
                    this.hfileScanner, e);

            return endOfData();
        }
    }

    public static Iterator<Cell> createIterator(FileSystem fs, Path path) {
        try {
            HFileScanner scanner = createScanner(fs, path);
            return new JanusHFileIterator(scanner);
        } catch (Exception e) {
            log.warn("Failed to create cells iterator for HFile due to exception (file: {}, exception: {})",
                    path, e);
            return Collections.emptyIterator();
        }
    }

    public static HFileScanner createScanner(FileSystem fs, Path path) {
        Configuration config = fs.getConf();

        HFile.Reader reader;
        try {
            config.setFloat("hfile.block.cache.size", 0);
            CacheConfig cacheConfig = new CacheConfig(config);

            reader = HFile.createReader(fs, path, cacheConfig, true, config);
            HFileScanner scanner = reader.getScanner(false, false);
            scanner.seekTo();
            return scanner;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CacheConfig getCacheConfig(Configuration config) {
        config.setFloat("hfile.block.cache.size", 0);

        return new CacheConfig(config);
    }
}
