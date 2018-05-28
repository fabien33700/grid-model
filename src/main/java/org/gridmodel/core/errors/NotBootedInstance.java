package org.gridmodel.core.errors;

public class NotBootedInstance extends GridModelException {

    public NotBootedInstance() {
        super("The instance isn't booted yet. Please add at least one file to process with append().");
    }
}
