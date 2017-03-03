package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;

public class Field implements Persistable<Integer> {
    public static final String TABLE_NAME = "FIELD";
    public static final String ID_COLUMN = "field_id";

    private Integer id;
    private String name;

    public Field() {
    }

    public Field(Integer id) {
        this.id = id;
    }

    public Field(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}
