package org.gridmodel.core.errors;

public class AlreadyBootedInstance extends GridModelException {
    public AlreadyBootedInstance() {
        super("An already booted instance cannot be configured anymore.");
    }
}
