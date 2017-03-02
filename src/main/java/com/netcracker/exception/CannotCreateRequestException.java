package com.netcracker.exception;

/**
 * Created by Nuts on 2/27/2017
 * 3:48 PM.
 */
public class CannotCreateRequestException extends BaseException {

    private static final String MESSAGE = "Cannot create request.";

    public CannotCreateRequestException(String description) {
        super(MESSAGE, description);
    }
}
