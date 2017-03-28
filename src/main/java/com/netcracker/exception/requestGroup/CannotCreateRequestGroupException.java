package com.netcracker.exception.requestGroup;

import com.netcracker.exception.BaseException;

public class CannotCreateRequestGroupException extends BaseException {
    private static final String MESSAGE = "Cannot create request group";

    public CannotCreateRequestGroupException(String description) {
        super(MESSAGE, description);
    }
}
