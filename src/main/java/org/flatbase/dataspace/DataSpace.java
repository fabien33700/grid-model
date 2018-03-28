package org.flatbase.dataspace;

import org.flatbase.index.IndexMap;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

public interface DataSpace {
    Set<String> columns();

    NavigableMap<Long, Map<String, String>> data();

    IndexMap index();

    long sequence();

    void incSequence();

    default long volume() {
        return data().size();
    }

    default long memoryUsage() {
        return 0L;
    }
}
