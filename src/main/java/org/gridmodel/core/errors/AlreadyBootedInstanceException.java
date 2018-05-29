package org.gridmodel.core.errors;

public class AlreadyBootedInstanceException extends GridModelException {
    public AlreadyBootedInstanceException() {
        super("An already booted instance cannot be configured anymore.");
    }
}
