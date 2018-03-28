package org.flatbase.query.criteria;

import org.flatbase.index.IndexMap;
import org.flatbase.query.QueryException;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class Criterion {
    private final String columnName;
    private final CriterionOperation operation;

    Criterion(String columnName, CriterionOperation operation) {
        this.columnName = columnName;
        this.operation = operation;
    }

    public String getColumnName() {
        return columnName;
    }

    public CriterionOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return columnName + operation.getDescription();
    }

    public List<Long> execute(IndexMap<Object> sourceIndex)
    throws QueryException {
        try {
            return operation.getFunction().apply(sourceIndex.get(columnName));
        } catch (ClassCastException ex) {
            List<String> types = parseIncompatibleTypes(ex);
            throw new QueryException(types.get(0), types.get(1), columnName);
        }
    }

    private List<String> parseIncompatibleTypes(ClassCastException ex) {
        final String REGEX = "(.*) cannot be cast to (.*)";
        Matcher m = Pattern.compile(REGEX).matcher(ex.getMessage());

        return m.find() ? asList(m.group(1), m.group(2)) : emptyList();
    }
}
