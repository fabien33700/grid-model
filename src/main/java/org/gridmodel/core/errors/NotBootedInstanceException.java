package org.gridmodel.core.errors;

public class NotBootedInstanceException extends GridModelException {

    public NotBootedInstanceException() {
        super("The instance isn't booted yet. Please add at least one file to process with append().");
    }
}
