package org.flatbase.query.criteria;

import java.util.List;
import java.util.NavigableMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class CriterionOperation  {

    interface OperationFunction extends Function<NavigableMap<Object, List<Long>>, List<Long>> {}

    private final String description;
    private final OperationFunction operation;

    CriterionOperation(String description,
                       OperationFunction operation) {
        this.description = description;
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public OperationFunction getFunction() {
        return operation;
    }

    public Criterion assign(String columnName) {
        return new Criterion(columnName, this);
    }
}
