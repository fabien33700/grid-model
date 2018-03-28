package org.flatbase.index;

import java.util.Comparator;
import java.util.function.Function;

public interface IndexDefinition<T> {
    Function<String, T> dataAdapter();
    Comparator<T> keyComparator();
}
