package org.flatbase.model;

import org.flatbase.index.IndexStructure;

import java.util.*;

public class DataspaceImpl implements Dataspace {
    private long sequence = 0L;

    private final List<String> columns;
    private final NavigableMap<Long, Row> data;
    private final Map<String, IndexStructure<?>> structures;

    public DataspaceImpl() {
        columns = new ArrayList<>();
        data = new TreeMap<>(Long::compareTo);
        structures = new HashMap<>();
    }

    @Override
    public List<String> columns() {
        return columns;
    }

    @Override
    public NavigableMap<Long, Row> data() {
        return data;
    }

    @Override
    public Map<String, IndexStructure<?>> structures() {
        return structures;
    }

    @Override
    public long nextVal() {
        return sequence++;
    }

    @Override
    public long rowCount() {
        return data.size();
    }

    @Override
    public void addColumn(String columnName) {
        if (!columns.contains(columnName)) {
            columns.add(columnName);
        }
    }
}
