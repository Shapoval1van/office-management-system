package com.netcracker.exception;

public abstract class BaseRuntimeException extends RuntimeException {
    private String description;

    public BaseRuntimeException(String message, String description) {
        super(message + ": " + description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
