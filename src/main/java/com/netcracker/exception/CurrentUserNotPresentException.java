package com.netcracker.exception;

public class CurrentUserNotPresentException extends ResourceNotFoundException {

    public CurrentUserNotPresentException(String description) {
        super(description);
    }
}
