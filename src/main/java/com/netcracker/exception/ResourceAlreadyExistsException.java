package com.netcracker.exception;


public class ResourceAlreadyExistsException extends BaseException {

    private static final String MESSAGE = "Resource already exists.";

    public ResourceAlreadyExistsException (String description) {
        super(MESSAGE, description);
    }
}
