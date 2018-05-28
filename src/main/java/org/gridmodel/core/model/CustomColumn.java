package org.gridmodel.core.model;

import java.util.function.Function;

/**
 * Represents the behavior of a custom column :
 *   a function that returns a value from an ExtractionContext
 * @param <T> The type of the custom column values
 * @author Fabien
 */
public interface CustomColumn<T extends Comparable<T>>
        extends Function<ExtractionContext, T> {}
