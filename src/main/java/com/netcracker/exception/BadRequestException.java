package com.netcracker.exception;


public class BadRequestException extends BaseException {
    private static final String MESSAGE = "Bad request.";

    public BadRequestException(String description) {
        super(MESSAGE, description);
    }
}
