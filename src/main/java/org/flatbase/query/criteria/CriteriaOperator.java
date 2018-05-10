package org.flatbase.query.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public enum CriteriaOperator {
    AND (CriteriaOperator::intersection),
    OR  (CriteriaOperator::union),
    XOR (CriteriaOperator::symmetricDiff);

    BinaryOperator<List<Long>> combinator;

    CriteriaOperator(BinaryOperator<List<Long>> combinator) {
        this.combinator = combinator;
    }

    public BinaryOperator<List<Long>> getCombinator() {
        return combinator;
    }

    public static <T> List<T> intersection(List<T> first, List<T> second) {
        return first.stream()
                .filter(second::contains)
                .collect(Collectors.toList());
    }

    public static <T> List<T> union(List<T> first, List<T> second) {
        List<T> results = new ArrayList<>();
        results.addAll(first);
        results.addAll(second);

        return results;
    }

    public static <T> List<T> symmetricDiff(List<T> first, List<T> second) {
        return first.stream()
                .filter(item -> !second.contains(item))
                .collect(Collectors.toList());
    }
}
