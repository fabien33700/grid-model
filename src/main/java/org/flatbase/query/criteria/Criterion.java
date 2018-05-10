package org.flatbase.query.criteria;

import org.flatbase.index.IndexStructure;
import org.flatbase.query.QueryException;

import java.util.List;
import java.util.Map;

import static org.flatbase.misc.Utils.parseIncompatibleTypes;

public class Criterion<T extends Comparable<T>> {
    private final String columnName;
    private final CriterionOperation<T> operation;

    Criterion(String columnName, CriterionOperation<T> operation) {
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

    public List<Long> execute(Map<String, IndexStructure<T>> sourceIndex)
    throws QueryException {
        try {
            return operation.getFunction().apply(sourceIndex.get(columnName));
        } catch (ClassCastException ex) {
            List<String> types = parseIncompatibleTypes(ex);
            throw new QueryException(types.get(0), types.get(1), columnName);
        }
    }
}
