package com.netcracker.exception;


public class CannotDeleteRequestException extends BaseException{

    private static final String MESSAGE = "Cannot delete request.";

    public CannotDeleteRequestException(String description) {
        super(MESSAGE, description);
    }
}
