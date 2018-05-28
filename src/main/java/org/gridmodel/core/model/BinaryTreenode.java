package org.gridmodel.core.model;

import java.util.Optional;

@SuppressWarnings("ALL")
public class BinaryTreenode<T> {
    private BinaryTreenode<T> left;
    private BinaryTreenode<T> right;
    private BinaryTreenode<T> parent;
    private T leafValue = null;

    public BinaryTreenode() {}

    public BinaryTreenode(T leafValue) {
        this.leafValue = leafValue;
    }

    public Optional<BinaryTreenode<T>> left() {
        return Optional.ofNullable(left);
    }

    public void setLeft(BinaryTreenode<T> left) {
        left.setParent(this);
        this.left = left;
        this.leafValue = null;
    }

    public void setLeft(T leftValue) {
        setLeft(new BinaryTreenode<>(leftValue));
    }

    public Optional<BinaryTreenode<T>> right() {
        return Optional.ofNullable(right);
    }

    public void setRight(BinaryTreenode<T> right) {
        right.setParent(this);
        this.right = right;
        this.leafValue = null;
    }

    public void setRight(T rightValue) {
        setRight(new BinaryTreenode<>(rightValue));
    }

    public Optional<T> leafValue() {
        return Optional.ofNullable(leafValue);
    }

    public void setLeafValue(T leafValue) {
        this.leafValue = leafValue;
    }

    public Optional<BinaryTreenode<T>> parent() {
        return Optional.ofNullable(parent);
    }

    public void setParent(BinaryTreenode<T> parent) {
        this.parent = parent;
    }

    public boolean isRoot() {
        return !parent().isPresent();
    }

    public boolean isLeaf() {
        return !left().isPresent() && !right().isPresent();
    }

    public boolean hasValue() {
        return leafValue().isPresent();
    }

    public boolean isEmpty() { return isRoot() && !hasValue()
            && !left().isPresent() && !right().isPresent(); }
}
