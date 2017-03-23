package com.netcracker.exception;

/**
 * Created by andrey on 21.03.2017.
 */
public class CannotDeleteUserException extends BaseException {
    private static final String MESSAGE = "Cannot delete user.";

    public CannotDeleteUserException(String description) {
        super(MESSAGE, description);
    }
}