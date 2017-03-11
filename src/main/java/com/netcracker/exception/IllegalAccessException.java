package com.netcracker.exception;

/**
 * Created by Max on 10.03.2017.
 */
public class IllegalAccessException extends BaseException {

    private static final String ILLEGAL_ACCESS_EXCEPTION_MESSAGE = "Illegal access";

    public IllegalAccessException(String description) {
        super(ILLEGAL_ACCESS_EXCEPTION_MESSAGE, description);
    }
}
