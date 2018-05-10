package org.flatbase.core;

import org.flatbase.index.IndexDefinition;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private final Map<String, IndexDefinition<?>> indexes;

    public Configuration() {
        this.indexes = new HashMap<>();
    }

    Map<String, IndexDefinition<?>> indexes() {
        return indexes;
    }
}
