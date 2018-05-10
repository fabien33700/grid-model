package org.flatbase.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class IndexStructure<T extends Comparable<T>> extends TreeMap<T, Set<Long>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IndexDefinition<T> definition;

    public IndexStructure(IndexDefinition<T> definition) {
        super(T::compareTo);
        Objects.requireNonNull(definition, "index.definition");
        this.definition = definition;
    }

    /**
     * Appends the value in the index.
     * @param value The raw value
     * @param primary The row index
     */
    public void append(String value, Long primary) {
        try {
            // Transform the raw string value to a comparable value
            T adapted = definition.adapter().apply(value);

            // If the index has no entry yet for this value, creating the ids container
            if (!containsKey(adapted)) {
                put(adapted, new HashSet<>());
            }
            // Adding the row id to the index
            get(adapted).add(primary);
        } catch (Exception e) {
            logger.debug("Could not adapt the raw value {} for column {} at row index {}",
                    value, definition.getColumnName(), primary);
        }
    }
}
