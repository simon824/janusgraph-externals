package org.janusgraph.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.util.FSUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) throws IOException {
        String regionEdgesFamilyPath = "hdfs://xxxx:9000/hbase/data/default/janustest/46a434dabe14ce334200f62ac5d8815f/e/e7d1cae78b0148218629b2eb4ac45d21";
        Path path = new Path(regionEdgesFamilyPath);
        FileSystem fs = path.getFileSystem(new Configuration());
        FileStatus[] fileStatuses = fs.listStatus(path, new FSUtils.HFileFilter(fs));
        System.out.println(fileStatuses);




        List<Iterator<Cell>> collect = Arrays.stream(fileStatuses)
                .map(FileStatus::getPath)
                .map(hfilePath -> JanusHFileIterator.createIterator(fs, hfilePath))
                .filter(Iterator::hasNext)
                .collect(Collectors.toList());

        for (Iterator<Cell> cellIterator : collect) {
            while(cellIterator.hasNext()){
                Cell next = cellIterator.next();
                System.out.println(next.toString());
            }
        }


    }
}

