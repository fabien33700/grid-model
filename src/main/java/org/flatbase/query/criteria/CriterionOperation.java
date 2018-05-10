package org.flatbase.query.criteria;

import org.flatbase.index.IndexStructure;

import java.util.List;
import java.util.function.Function;

public final class CriterionOperation<T extends Comparable<T>>  {

    interface OperationFn<T extends Comparable<T>> extends Function<IndexStructure<T>, List<Long>> {}

    private final String description;
    private final OperationFn<T> operation;

    CriterionOperation(String description,
                       OperationFn<T> operation) {
        this.description = description;
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public OperationFn<T> getFunction() {
        return operation;
    }

    public Criterion<T> assign(String columnName) {
        return new Criterion<>(columnName, this);
    }
}
