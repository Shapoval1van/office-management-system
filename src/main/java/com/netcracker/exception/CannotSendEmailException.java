package com.netcracker.exception;

public class CannotSendEmailException extends RuntimeException {
    private static final String MESSAGE = "Email sending error.";

    public CannotSendEmailException() {
        super(MESSAGE);
    }
}
