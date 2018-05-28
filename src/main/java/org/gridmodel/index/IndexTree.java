package org.gridmodel.index;

import java.util.NavigableMap;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents the behavior of an index tree.
 * Index tree is the memory representation of an index, built as a associative array of
 *   indexed values as keys and primary keys as values.
 * @param <I> The type of the input value
 * @param <T> The typed of the indexed value
 */
public interface IndexTree<I, T extends Comparable<T>> extends NavigableMap<T, Set<Long>> {

    void append(I input, Long primary);
    Function<I, T> adapter();

    String columnName();
}
