package org.flatbase.index;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class IndexMap<T> extends HashMap<String, NavigableMap<T, List<Long>>> {

    public IndexMap() {
        super();
    }

    public IndexMap(Map<String, NavigableMap<T, List<Long>>> source) {
        super(source);
    }

    public boolean putIndexData(String columnName,
                                Long primary, String rawValue,
                                IndexDefinition<T> definition) {
        if (!containsKey(columnName))
            put(columnName, new TreeMap<>(definition.keyComparator()));

        T converted = definition.dataAdapter().apply(rawValue);
        if (!get(columnName).containsKey(converted))
            get(columnName).put(converted, new ArrayList<>());

        return get(columnName).get(converted).add(primary);
    }

    /*public List<Long> getIndexData(String columnName, T reference) {
        return (containsKey(columnName)) && (get(columnName).containsKey(reference)) ?
             unmodifiableList(get(columnName).get(reference)) : emptyList();
    }*/

    public boolean hasColumn(String columnName) {
        return containsKey(columnName);
    }
}
