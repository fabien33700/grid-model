package org.gridmodel.query.results;

import org.gridmodel.core.model.Row;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@FunctionalInterface
public interface ResultTransformer<R> extends Function<Row, R> {
    default List<R> apply(List<Row> rows) {
        return rows.stream()
                .map(this)
                .collect(Collectors.toList());
    }
}
