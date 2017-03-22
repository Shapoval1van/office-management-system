package com.netcracker.exception.request;

import com.netcracker.exception.BaseException;

public class RequestNotAssignedException extends BaseException {
    private static final String MESSAGE = "Request not assigned";

    public RequestNotAssignedException(String description) {
        super(MESSAGE, description);
    }
}
