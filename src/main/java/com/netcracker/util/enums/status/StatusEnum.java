package com.netcracker.util.enums.status;

public enum StatusEnum {
    FREE("FREE"),
    IN_PROGRESS("IN PROGRESS"),
    CLOSED("CLOSED"),
    CANCELED("CANCELED");

    private String name;

    private StatusEnum(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
