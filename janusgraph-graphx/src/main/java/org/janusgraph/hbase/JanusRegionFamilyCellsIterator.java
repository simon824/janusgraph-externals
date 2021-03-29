package org.janusgraph.hbase;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.UnmodifiableIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhangshiming
 */
public class JanusRegionFamilyCellsIterator implements Iterator<Cell> {

    private Comparator<Cell> ASC_CELL_COMPARATOR = (left, right) -> {
        int c = CellComparator.getInstance().compareRows(left, right);

        if (c != 0) {
            return c;
        } else {
            if (left.getFamilyLength() + left.getQualifierLength() == 0 &&
                    left.getTypeByte() == KeyValue.Type.Minimum.getCode()) {
                return 1;
            } else if (right.getFamilyLength() + right.getQualifierLength() == 0 &&
                    right.getTypeByte() == KeyValue.Type.Minimum.getCode()) {
                return -1;
            } else {
                boolean sameFamilySize = left.getFamilyLength() == right.getFamilyLength();
                if (!sameFamilySize) {
                    return Bytes.compareTo(left.getFamilyArray(), left.getFamilyOffset(), left.getFamilyLength(),
                            right.getFamilyArray(), right.getFamilyOffset(), right.getFamilyLength());
                } else {
                    int diff = CellComparator.getInstance().compareQualifiers(left, right);

                    if (diff != 0) {
                        return diff;
                    } else {
                        diff = CellComparator.getInstance().compareTimestamps(right, left); // Different from CellComparator.compare()
                        return diff != 0 ? diff : (255 & right.getTypeByte()) - (255 & left.getTypeByte());
                    }
                }
            }
        }
    };

    private final PeekingIterator<Cell> sortedRegionIterator;

    public JanusRegionFamilyCellsIterator(String regionEdgesFamilyPath) throws IOException {
        sortedRegionIterator = createSortedHFilesIterator(regionEdgesFamilyPath);
    }

    protected PeekingIterator<Cell> createSortedHFilesIterator(String regionEdgesFamilyPath) throws IOException {
        Iterable<Iterator<Cell>> hFilesIterators = createHFilesIterators(regionEdgesFamilyPath);
        UnmodifiableIterator<Cell> cellUnmodifiableIterator = Iterators.mergeSorted(hFilesIterators, ASC);
        PeekingIterator<Cell> cellPeekingIterator = Iterators.peekingIterator(cellUnmodifiableIterator);
        return cellPeekingIterator;
    }

    private Comparator<Cell> ASC = (left, right) -> {
        return 1;
    };

    protected Iterable<Iterator<Cell>> createHFilesIterators(String regionEdgesFamilyPath) throws IOException {
        Path path = new Path(regionEdgesFamilyPath);
        FileSystem fs = path.getFileSystem(new Configuration());
        FileStatus[] fileStatuses = fs.listStatus(path, new FSUtils.HFileFilter(fs));
        System.out.println(fileStatuses);
        List<Iterator<Cell>> collect = Arrays.stream(fileStatuses)
                .map(FileStatus::getPath)
                .map(hfilePath -> JanusHFileIterator.createIterator(fs, hfilePath))
                .filter(Iterator::hasNext)
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public boolean hasNext() {
        return sortedRegionIterator.hasNext();
    }

    @Override
    public Cell next() {
        Cell mostUpdated = sortedRegionIterator.next();

        while (sortedRegionIterator.hasNext() && equalsRowFamilyQualifier(mostUpdated, sortedRegionIterator.peek())) {
            mostUpdated = sortedRegionIterator.next();
        }

        return mostUpdated;
    }


    private boolean equalsRowFamilyQualifier(Cell left, Cell right) {

        int i = CellComparator.getInstance().compareQualifiers(left, right);
        int j = CellComparator.getInstance().compareFamilies(left, right);
        int h = CellComparator.getInstance().compareRows(left, right);

        return i >= 0 && j >= 0 && h >= 0;
    }
}
