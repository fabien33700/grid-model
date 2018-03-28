package org.flatbase.query.criteria;

import org.flatbase.utils.ListUtils;

import java.util.List;
import java.util.function.BinaryOperator;

public enum CriteriaOperator {
    AND (ListUtils::intersection),
    OR  (ListUtils::union),
    XOR (ListUtils::symmetricDiff);

    BinaryOperator<List<Long>> combinator;

    CriteriaOperator(BinaryOperator<List<Long>> combinator) {
        this.combinator = combinator;
    }

    public BinaryOperator<List<Long>> getCombinator() {
        return combinator;
    }
}
