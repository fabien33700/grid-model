package org.gridmodel.query;

import org.gridmodel.core.Instance;
import org.gridmodel.core.model.Row;
import org.gridmodel.query.criteria.Criteria;
import org.gridmodel.query.criteria.CriteriaOperator;
import org.gridmodel.query.criteria.Criterion;
import org.gridmodel.query.criteria.CriterionOperation;
import org.gridmodel.query.results.ResultStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.gridmodel.misc.Utils.format;

public class QueryBuilder {

    private static final String NOT_INDEXED_COLUMN = "Querying is only possible on indexed columns, {} is not one.";
    private static final String NO_PRIOR_WHERE = "Logical operators cannot be used without a prior use of where() clause.";
    private static final String SEVERAL_WHERE = "The where() clause must be unique and the first statement of all queries.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Instance instance;
    private final List<String> columns;
    private Criteria criteria;


    public QueryBuilder(Instance instance, String... columns) {
        this.instance = instance;
        this.columns = (columns.length > 0) ?
            asList(columns) : new ArrayList<>(instance.dataspace().columns());

        this.criteria = new Criteria();
    }

    @SuppressWarnings("unchecked")
    public QueryBuilder where(String columnName, CriterionOperation criterion) {
        checkColumnIndexation(columnName);

        if (!criteria.isLeaf() || criteria.hasValue())
            throw new IllegalStateException(SEVERAL_WHERE);

        criteria.setLeafValue(criterion.assign(columnName));
        return this;
    }

    private void checkColumnIndexation(String columnName)
            throws UnsupportedOperationException {

        if (!instance.dataspace().indexesTrees().containsKey(columnName))
            throw new UnsupportedOperationException(format(NOT_INDEXED_COLUMN, columnName));
    }

    private void aggregateCriteria(String columnName,
                                   CriterionOperation criterion,
                                   CriteriaOperator operator) {
        if (criteria.isLeaf()) {
            if (!criteria.leafValue().isPresent())
                throw new IllegalStateException(NO_PRIOR_WHERE);

            criteria.setLeft(criteria.leafValue().get());
            criteria.setRight(criterion.assign(columnName));
            criteria.setOperator(operator);
        } else {
            Criteria upper = new Criteria();
            upper.setLeft(criteria);
            upper.setRight(new Criteria(criterion.assign(columnName)));
            upper.setOperator(operator);
            criteria = upper;
        }
    }

    public QueryBuilder and(String columnName, CriterionOperation criterion) {
        checkColumnIndexation(columnName);
        aggregateCriteria(columnName, criterion, CriteriaOperator.AND);

        return this;
    }

    public QueryBuilder or(String columnName, CriterionOperation criterion) {
        checkColumnIndexation(columnName);
        aggregateCriteria(columnName, criterion, CriteriaOperator.OR);

        return this;
    }

    public QueryBuilder exclusiveOr(String columnName, CriterionOperation criterion) {
        checkColumnIndexation(columnName);
        aggregateCriteria(columnName, criterion, CriteriaOperator.XOR);

        return this;
    }

    private Row selectColumns(Row sourceMap) {
        return (columns.isEmpty()) ? sourceMap :
                new Row(sourceMap.entrySet().stream()
                        .filter(entry -> columns.contains(entry.getKey()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @SuppressWarnings("unchecked")
    public ResultStore fetch() {
        long start = currentTimeMillis();
        List<Long> primaries = new ArrayList<>();

        Criteria cursor = criteria;
        if (cursor.isEmpty()) {
            primaries.addAll(instance.dataspace().data().keySet());
        } else if (cursor.isLeaf()) {
            if (cursor.leafValue().isPresent()) {
                Criterion criterion = cursor.leafValue().get();
                primaries.addAll(criterion.execute(instance.dataspace().indexesTrees()));
            }
        } else {
            primaries.addAll(criteria.combine(instance.dataspace().indexesTrees()));
        }

        List<Row> results = primaries.stream()
                .map(instance.dataspace().data()::get)
                .map(this::selectColumns)
                .collect(Collectors.toList());

        long execTime = currentTimeMillis() - start;
        long count = primaries.size();
        String query = criteria.toString();

        return new ResultStore(results, execTime, count, query, columns);
    }
}
