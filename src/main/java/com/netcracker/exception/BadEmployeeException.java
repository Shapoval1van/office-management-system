package com.netcracker.exception;


public class BadEmployeeException extends BaseException {

    private static final String MESSAGE = "Bad employee for token.";

    public BadEmployeeException (String description) {
        super(MESSAGE, description);
    }
}
