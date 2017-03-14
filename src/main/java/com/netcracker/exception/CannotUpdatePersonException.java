package com.netcracker.exception;


public class CannotUpdatePersonException extends BaseException{

    private static final String MESSAGE = "Cannot update user.";

    public CannotUpdatePersonException(String description) {
        super(MESSAGE, description);
    }
}
