package com.netcracker.exception;

public class CannotAssignRequestException extends BaseException {

    private static final String MESSAGE = "Cannot assign request.";

    public CannotAssignRequestException(String description) {
        super(MESSAGE, description);
    }
}
