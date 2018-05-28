package org.gridmodel.core;

import org.gridmodel.core.model.CustomColumn;
import org.gridmodel.index.IndexAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * A class which holds the instance configuration.
 * @author Fabien
 */
public class Configuration {
    /**
     * The indexes configuration
     */
    private final Map<String, IndexAdapter<?>> indexes;

    /**
     * The custom columns configuration
     */
    private final Map<String, CustomColumn<?>> customColumns;

    /**
     * Configuration constructor
     */
    public Configuration() {
        this.customColumns = new HashMap<>();
        this.indexes = new HashMap<>();
    }

    Map<String, IndexAdapter<?>> indexes() {
        return indexes;
    }

    Map<String, CustomColumn<?>> customColumns() { return customColumns; }
}
