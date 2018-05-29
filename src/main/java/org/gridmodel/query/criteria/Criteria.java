package org.gridmodel.query.criteria;

import org.gridmodel.core.model.BinaryTreenode;
import org.gridmodel.index.IndexTree;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.gridmodel.misc.Utils.orElse;

public class Criteria extends BinaryTreenode<Criterion> {

    private CriteriaOperator operator;

    public Criteria() {
        super();
    }

    public Criteria(Criterion leafValue) {
         super(leafValue);
    }

    public CriteriaOperator getOperator() {
        return operator;
    }

    public void setOperator(CriteriaOperator operator) {
        this.operator = operator;
    }

    @Override
    public void setLeft(Criterion leftValue) {
        setLeft(new Criteria(leftValue));
    }

    @Override
    public void setRight(Criterion rightValue) {
        setRight(new Criteria(rightValue));
    }

    @Override
    public String toString() {
        return isLeaf() ?
                orElse(leafValue(), Criterion::toString, "") :
                String.join(" ", asList(
                    orElse(left(),  BinaryTreenode<Criterion>::toString, ""),
                    getOperator().name(),
                    orElse(right(), BinaryTreenode<Criterion>::toString, "")));
    }

    @SuppressWarnings("unchecked")
    public List<Long> combine(Map<String, IndexTree<?, ?>> sourceIndex) {
        if (isLeaf()) {
            return orElse(leafValue(), c -> c.execute(sourceIndex), emptyList());
        }

        if (left().isPresent() && right().isPresent()) {
            Criteria leftCrit = (Criteria) left().get();
            Criteria rightCrit = (Criteria) right().get();
            return operator.combinator.apply(
                    leftCrit.combine(sourceIndex),
                    rightCrit.combine(sourceIndex));
        }

        throw new IllegalStateException("Incoherent : must have two operands.");
    }
}
