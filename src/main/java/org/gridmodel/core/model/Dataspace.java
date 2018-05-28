package org.gridmodel.core.model;

import org.gridmodel.index.IndexTree;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

public interface Dataspace {
    List<String> columns();

    NavigableMap<Long, Row> data();

    Map<String, IndexTree<?, ?>> indexesTrees();

    long nextVal();

    default long rowCount() {
        return data().size();
    }

    void addColumn(String columnName);
}
