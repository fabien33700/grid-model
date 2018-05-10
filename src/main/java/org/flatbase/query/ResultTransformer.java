package org.flatbase.query;

import org.flatbase.model.Row;

import java.util.List;
import java.util.stream.Collectors;

public interface ResultTransformer<R> {
    R transform(Row row);

    default List<R> transformRows(List<Row> rows) {
        return rows.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }
}
