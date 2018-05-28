package org.gridmodel.index;

import java.util.function.Function;

/**
 * Represents the behavior of an index adapter.
 * Index adapter is a function that convert the raw value string to a T typed value.
 * T value must be Comparable, to allow index to sort values correctly.
 * @param <T> The type of the indexed value.
 */
public interface IndexAdapter<T extends Comparable<T>>
        extends Function<String, T> {}
