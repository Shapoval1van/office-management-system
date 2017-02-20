package com.netcracker.exception;


public class ResourceNotFoundException extends BaseException {

    private static final String MESSAGE = "Resource not found.";

    public ResourceNotFoundException(String description) {
        super(MESSAGE, description);
    }
}
