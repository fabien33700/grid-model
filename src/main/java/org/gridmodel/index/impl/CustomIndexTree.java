package org.gridmodel.index.impl;

import org.gridmodel.core.model.CustomColumn;
import org.gridmodel.core.model.ExtractionContext;
import org.gridmodel.index.IndexTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

public class CustomIndexTree<T extends Comparable<T>>
        extends TreeMap<T, Set<Long>>
        implements IndexTree<ExtractionContext, T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String columnName;

    private final CustomColumn<T> adapter;

    public CustomIndexTree(String columnName, CustomColumn<T> adapter) {
        super(T::compareTo);
        Objects.requireNonNull(adapter, "index.adapter");
        this.adapter = adapter;
        this.columnName = columnName;
    }

    @Override
    public Function<ExtractionContext, T> adapter() {
        return adapter;
    }

    @Override
    public String columnName() {
        return columnName;
    }

    /**
     * Appends the value in the index.
     * @param primary The row index
     */
    public void append(ExtractionContext context, Long primary) {
        T value = adapter.apply(context);
        // If the index has no entry yet for this value, creating the ids container
        if (!containsKey(value)) {
            put(value, new HashSet<>());
        }
        // Adding the row id to the index
        get(value).add(primary);
    }
}
