package org.flatbase.model;

import org.flatbase.index.IndexStructure;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public interface Dataspace {
    List<String> columns();

    NavigableMap<Long, Row> data();

    Map<String, IndexStructure<?>> structures();

    long nextVal();

    default long rowCount() {
        return data().size();
    }

    void addColumn(String columnName);
}
