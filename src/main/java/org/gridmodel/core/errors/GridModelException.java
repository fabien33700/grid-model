package org.gridmodel.core.errors;

public abstract class GridModelException extends RuntimeException {
    protected GridModelException(String message) {
        super(message);
    }
}
