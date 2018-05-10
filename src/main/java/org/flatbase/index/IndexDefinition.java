package org.flatbase.index;

import java.util.function.Function;

public class IndexDefinition<T extends Comparable<T>> {

    private String columnName;
    private Function<String, T> adapter;

    public IndexDefinition(String columnName,
                           Function<String, T> adapter) {
        this.adapter = adapter;
        this.columnName = columnName;
    }

    public Function<String, T> adapter() {
        return adapter;
    }

    public String getColumnName() {
        return columnName;
    }
}
