package org.flatbase.utils;

import java.util.Optional;

public class BinaryTreenodeBase<T> implements BinaryTreenode<T> {
    private BinaryTreenode<T> left;
    private BinaryTreenode<T> right;
    private BinaryTreenode<T> parent;
    private T leafValue = null;

    public BinaryTreenodeBase() {}

    public BinaryTreenodeBase(T leafValue) {
        this.leafValue = leafValue;
    }

    @Override
    public Optional<BinaryTreenode<T>> left() {
        return Optional.ofNullable(left);
    }

    @Override
    public void setLeft(BinaryTreenode<T> left) {
        left.setParent(this);
        this.left = left;
        this.leafValue = null;
    }

    @Override
    public void setLeft(T leftValue) {
        setLeft(new BinaryTreenodeBase<>(leftValue));
    }

    @Override
    public Optional<BinaryTreenode<T>> right() {
        return Optional.ofNullable(right);
    }

    @Override
    public void setRight(BinaryTreenode<T> right) {
        right.setParent(this);
        this.right = right;
        this.leafValue = null;
    }

    @Override
    public void setRight(T rightValue) {
        setRight(new BinaryTreenodeBase<>(rightValue));
    }

    @Override
    public Optional<T> leafValue() {
        return Optional.ofNullable(leafValue);
    }

    @Override
    public void setLeafValue(T leafValue) {
        this.leafValue = leafValue;
    }

    @Override
    public Optional<BinaryTreenode<T>> parent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public void setParent(BinaryTreenode<T> parent) {
        this.parent = parent;
    }
}
