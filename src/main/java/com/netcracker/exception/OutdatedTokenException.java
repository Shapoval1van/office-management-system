package com.netcracker.exception;


public class OutdatedTokenException extends  BaseException {
    private static final String MESSAGE = "Token expired";

    public OutdatedTokenException(String description) {
        super(MESSAGE, description);
    }
}
