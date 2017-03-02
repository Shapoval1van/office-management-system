package com.netcracker.exception;


public class CannotCreateSubRequestException extends BaseException{

    private static final String MESSAGE = "Cannot create sub request.";

    public CannotCreateSubRequestException(String description) {
        super(MESSAGE, description);
    }
}
