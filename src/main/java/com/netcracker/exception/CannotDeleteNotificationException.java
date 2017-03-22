package com.netcracker.exception;


public class CannotDeleteNotificationException extends BaseException{

    private static final String MESSAGE = "Cannot delete request.";

    public CannotDeleteNotificationException(String description) {
        super(MESSAGE, description);
    }
}
