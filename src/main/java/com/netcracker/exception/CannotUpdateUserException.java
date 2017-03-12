package com.netcracker.exception;


public class CannotUpdateUserException extends BaseException{

    private static final String MESSAGE = "Cannot update user.";

    public CannotUpdateUserException(String description) {
        super(MESSAGE, description);
    }
}
