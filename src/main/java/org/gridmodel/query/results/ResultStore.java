package org.gridmodel.query.results;

import org.gridmodel.core.model.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

public class ResultStore {
    
    private static final String NEWLINE = "\r\n";
    
    private static final String SEP = ";";

    private final List<Row> rows;

    private final long execTime;

    private final long count;

    private final String query;

    private final List<String> columns;

    public ResultStore(List<Row> rows, long execTime, long count, String query, List<String> columns) {
        this.rows = unmodifiableList(rows);
        this.execTime = execTime;
        this.count = count;
        this.query = query;
        this.columns = unmodifiableList(columns);
        this.debug();
    }

    private void debug() {
        final Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("\nQuery :\t{}\nExecution time :\t {} ms\nRows count : \t{}",
                query, execTime, count);
    }

    public long getExecutionTime() {
        return execTime;
    }

    public long getRowsCount() {
        return count;
    }

    public String getQuery() {
        return query;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Row> rows() {
        return rows;
    }

    public <R> List<R> transform(ResultTransformer<R> transformer) {
        return transformer.apply(rows);
    }

    public List<?> pluck(String column) {
        return (columns.contains(column)) ?
            rows.stream()
                .filter(row -> row.containsKey(column))
                .map(row -> row.get(column))
                .filter(value -> value != null && !value.toString().isEmpty())
                .collect(Collectors.toList()) :
            emptyList();
    }

    public String asCsv() {
        StringBuilder sb = new StringBuilder()
            .append(String.join(SEP, columns))
            .append(NEWLINE);

        rows().stream().map(row -> columns.stream()
                .map(row::get)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(SEP)) + NEWLINE)
            .forEachOrdered(sb::append);

        return sb.toString();
    }

}
