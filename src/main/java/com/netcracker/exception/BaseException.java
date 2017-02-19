package com.netcracker.exception;


public abstract class BaseException extends Exception {

    private String description;

    public BaseException(String message, String description) {
        super(message);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
