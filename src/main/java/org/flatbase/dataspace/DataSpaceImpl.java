package org.flatbase.dataspace;

import org.flatbase.index.IndexMap;

import java.util.*;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableNavigableMap;

public class DataSpaceImpl implements DataSpace {
    private long sequence = 0L;

    private final Set<String> columns;
    private final NavigableMap<Long, Map<String, String>> data;
    private final IndexMap index;

    public DataSpaceImpl() {
        columns = new HashSet<>();
        data = new TreeMap<>(Long::compareTo);
        index = new IndexMap();
    }

    @Override
    public Set<String> columns() {
        return columns;
    }

    @Override
    public NavigableMap<Long, Map<String, String>> data() {
        return data;
    }

    @Override
    public IndexMap index() {
        return index;
    }

    @Override
    public long sequence() {
        return sequence;
    }

    @Override
    public void incSequence() {
        sequence++;
    }

    @Override
    public long volume() {
        return data.size();
    }

    @Override
    public long memoryUsage() {
        return 0;
    }
}
