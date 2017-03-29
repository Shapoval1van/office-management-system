package com.netcracker.exception;

public class CannotSendEmailException extends BaseRuntimeException {

    private static final String MESSAGE = "Cannot send email exception";

    public CannotSendEmailException(String description) {
        super(MESSAGE, description);
    }
}
