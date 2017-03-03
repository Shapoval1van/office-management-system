package com.netcracker.exception;

/**
 * Created by Max on 03.03.2017.
 */
public class CurrentUserNotPresentException extends ResourceNotFoundException {

    public CurrentUserNotPresentException(String description) {
        super(description);
    }
}
