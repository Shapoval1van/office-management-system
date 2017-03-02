package com.netcracker.model.entity;

import com.netcracker.repository.common.Persistable;

public class Status implements Persistable<Integer> {
    public static final String TABLE_NAME = "STATUS";
    public static final String ID_COLUMN = "status_id";

    private Integer id;
    private String name;

    public Status(){
    }

    public Status(int id) {
        this.id = id;
    }

    public Status(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        if (id != null ? !id.equals(status.id) : status.id != null) return false;
        return name != null ? name.equals(status.name) : status.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}