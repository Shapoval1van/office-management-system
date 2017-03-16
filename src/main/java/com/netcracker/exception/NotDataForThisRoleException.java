package com.netcracker.exception;


public class NotDataForThisRoleException extends BaseException{

    private static final String MESSAGE = "Cannot create request.";

    public NotDataForThisRoleException(String description) {
        super(MESSAGE, description);
    }
}
