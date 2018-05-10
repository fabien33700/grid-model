package org.flatbase.query;

import org.flatbase.core.Instance;
import org.flatbase.model.Row;
import org.flatbase.query.criteria.Criteria;
import org.flatbase.query.criteria.CriteriaOperator;
import org.flatbase.query.criteria.Criterion;
import org.flatbase.query.criteria.CriterionOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.flatbase.misc.Utils.format;

public class QueryBuilder {

    private static final String NOT_INDEXED_COLUMN = "Querying is only possible on indexed columns, {} is not one.";
    private static final String NO_PRIOR_WHERE = "Logical operators cannot be used without a prior use of where() clause.";
    private static final String SEVERAL_WHERE = "The where() clause must be unique and the first statement of all queries.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Instance instance;
    private List<String> columns;
    private Criteria criteria;


    public QueryBuilder(Instance instance, String... columns) {
        this.instance = instance;
        this.columns = asList(columns);
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

        if (!instance.dataspace().structures().containsKey(columnName))
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
    private List<Row> rawList() {
        long start = currentTimeMillis();
        List<Long> primaries = new ArrayList<>();

        Criteria cursor = criteria;
        if (cursor.isLeaf()) {
            if (cursor.leafValue().isPresent()) {
                Criterion criterion = cursor.leafValue().get();
                primaries.addAll(criterion.execute(instance.dataspace().structures()));
            }
        } else {
            primaries.addAll(criteria.combine(instance.dataspace().structures()));
        }

        List<Row> results = new ArrayList<>();
        if (!primaries.isEmpty()) {
            primaries.stream().map(instance.dataspace().data()::get)
                    .map(this::selectColumns)
                    .forEachOrdered(results::add);
        } else {
            results.addAll(instance.dataspace().data().values());
        }

        logger.debug("\nQuery :\t{}\nExecution time :\t {} ms\nRows count : \t{}",
                criteria.toString(), currentTimeMillis() - start, results.size());
        return results;
    }

    public List<Row> list() {
        return rawList();
    }

    public <T> List<T> list(ResultTransformer<T> transformer) {
        return transformer.transformRows(rawList());
    }

}
