package org.flatbase.query;

import org.flatbase.core.Instance;
import org.flatbase.query.criteria.Criteria;
import org.flatbase.query.criteria.CriteriaOperator;
import org.flatbase.query.criteria.Criterion;
import org.flatbase.query.criteria.CriterionOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toMap;
import static org.flatbase.utils.Utils.format;

public class QueryBuilder {

    private static final String NOT_INDEXED_COLUMN = "Querying is only possible on indexed columns, {} is not one.";
    private static final String NO_PRIOR_WHERE = "Logical operators cannot be used without a prior use of where() clause.";

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
            throw new IllegalStateException("The where() clause must be unique and the first statement of all queries.");

        criteria.setLeafValue(criterion.assign(columnName));
        return this;
    }

    private void checkColumnIndexation(String columnName)
            throws UnsupportedOperationException {
        if (!instance.dataspace().index().hasColumn(columnName))
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




    private Map<String, String> selectColumns(Map<String, String> sourceMap) {
        return (columns.isEmpty()) ? sourceMap :
                sourceMap.entrySet().stream()
                .filter(entry -> columns.contains(entry.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings("unchecked")
    public List<Object> list() {
        long start = currentTimeMillis();
        List<Object> results = new ArrayList<>();

        Map<Criterion, List<Long>> matches = new HashMap<>();
        extractCriteria(criteria, matches);

        List<Long> primaries = new ArrayList<>();

        Criteria cursor = criteria;
        if (cursor.isLeaf()) {
            if (cursor.leafValue().isPresent()) {
                Criterion criterion = cursor.leafValue().get();
                primaries.addAll(criterion.execute(instance.dataspace().index()));
            }
        } else {
            primaries.addAll(criteria.combine(instance.dataspace().index()));
        }

        primaries.stream().map(instance.dataspace().data()::get)
                .map(this::selectColumns)
                .forEachOrdered(results::add);



        /* Partie à déplacer */
        /*NavigableMap<Object, List<Long>> filteredMap;
        try {
            filteredMap = comp.apply(indexMap.get(columnName));
        } catch (ClassCastException ex) {
            List<String> types = parseIncompatibleTypes(ex);
            throw new QueryException(types.get(0), types.get(1), columnName);
        }

        List<Long> primaries = filteredMap
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        primaries.stream().map(instance.dataspace().data()::get)
                .map(this::selectColumns)
                .forEachOrdered(results::add);
        return Collections.unmodifiableList(results);*/
        instance.debug("Query `{}` executed in {} ms, {} row(s) returned.",
                criteria.toString(), currentTimeMillis() - start, results.size());
        return unmodifiableList(results);
    }


    private void extractCriteria(Criteria currentCriteria, Map<Criterion,List<Long>> matches) {

    }
}
