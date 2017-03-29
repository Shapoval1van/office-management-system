package com.netcracker.exception.requestGroup;


import com.netcracker.exception.BaseException;

public class CannotUpdateStatusException extends BaseException{

    private static final String MESSAGE = "You can not change the status of the request that is in the group.";

    public CannotUpdateStatusException(String description) {
        super(MESSAGE, description);
    }
}
