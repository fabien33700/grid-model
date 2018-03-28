package org.flatbase.utils;

import java.util.Optional;

public interface BinaryTreenode<T> {
    Optional<T> leafValue();
    Optional<BinaryTreenode<T>> left();
    Optional<BinaryTreenode<T>> right();
    Optional<BinaryTreenode<T>> parent();

    void setLeft(BinaryTreenode<T> left);
    void setLeft(T leftValue);

    void setRight(BinaryTreenode<T> right);
    void setRight(T rightValue);

    void setParent(BinaryTreenode<T> parent);
    void setLeafValue(T leafValue);

    default boolean isRoot() {
        return !parent().isPresent();
    }

    default boolean isLeaf() {
        return !left().isPresent() && !right().isPresent();
    }

    default boolean hasValue() {
        return leafValue().isPresent();
    }

    /*default BinaryTreenode<T> findRoot() {
        BinaryTreenode<T> cursor = this;
        while (cursor.parent().isPresent())
            cursor = cursor.parent().get();

        return cursor;
    }*/
}
