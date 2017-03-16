package com.netcracker.util.enums.status;

public enum StatusEnum {
    FREE(1, "FREE"),
    IN_PROGRESS(2, "IN PROGRESS"),
    CLOSED(3, "CLOSED"),
    CANCELED(4, "CANCELED");

    private Integer id;
    private String name;

     StatusEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
