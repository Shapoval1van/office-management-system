package com.netcracker.exception;


public class BadRequestException extends BaseException {
    private static final String MESSAGE = "Bad request.";
    private static final String DESCRIPTION = "";

    public BadRequestException(String description) {
        super(MESSAGE, description);
    }

    public BadRequestException() {
        super(MESSAGE, DESCRIPTION);
    }
}
