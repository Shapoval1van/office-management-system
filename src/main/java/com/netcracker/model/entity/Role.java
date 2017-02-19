package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;

public class Role implements Persistable<Long> {

    private static final String TABLE_NAME = "ROLE";
    private static final String ID_COLUMN = "role_id";

    private Long id;
    private String name;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
