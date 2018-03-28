package org.flatbase.index;

import java.util.Comparator;
import java.util.function.Function;

public class IndexDefinitionBase<T> implements IndexDefinition {

    private Function<String, T> adapter;
    private Comparator<T> comparator;

    public IndexDefinitionBase(Function<String, T> adapter,
                               Comparator<T> comparator) {
        this.adapter = adapter;
        this.comparator = comparator;
    }

    @Override
    public Function<String, T> dataAdapter() {
        return adapter;
    }

    @Override
    public Comparator<T> keyComparator() {
        return comparator;
    }
}
