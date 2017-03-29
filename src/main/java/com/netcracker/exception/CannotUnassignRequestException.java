package com.netcracker.exception;

public class CannotUnassignRequestException extends BaseException {

    private static final String MESSAGE = "Cannot unassign request.";

    public CannotUnassignRequestException(String description) {
        super(MESSAGE, description);
    }
}
