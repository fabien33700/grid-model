package org.gridmodel.index.impl;

import org.gridmodel.index.IndexAdapter;
import org.gridmodel.index.IndexTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Standard implementation for a index tree for columns.
 *
 * @param <T>
 */
public class ColumnIndexTree<T extends Comparable<T>>
        extends TreeMap<T, Set<Long>>
        implements IndexTree<String, T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String columnName;

    private final IndexAdapter<T> adapter;

    public ColumnIndexTree(String columnName, IndexAdapter<T> adapter) {
        super(T::compareTo);
        Objects.requireNonNull(adapter, "index.adapter");
        this.adapter = adapter;
        this.columnName = columnName;
    }

    @Override
    public Function<String, T> adapter() {
        return adapter;
    }

    @Override
    public String columnName() {
        return columnName;
    }

    /**
     * Appends the value in the index.
     * @param input The raw value
     * @param primary The row index
     */
    @Override
    public void append(String input, Long primary) {
        try {
            // Transform the raw string value to a comparable value
            T adapted = adapter.apply(input);

            // If the index has no entry yet for this value, creating the ids container
            if (!containsKey(adapted)) {
                put(adapted, new HashSet<>());
            }
            // Adding the row id to the index
            get(adapted).add(primary);
        } catch (Exception e) {
            logger.debug("Could not adapt the raw value {} for column {} at row index {}",
                    input, columnName, primary);
        }
    }
}
