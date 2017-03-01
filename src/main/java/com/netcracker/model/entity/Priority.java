package com.netcracker.model.entity;

import com.netcracker.repository.common.Persistable;

public class Priority implements Persistable<Integer> {
    public static final String TABLE_NAME = "PRIORITY";
    public static final String ID_COLUMN = "priority_id";

    private Integer id;
    private String name;

    public Priority(){
    }

    public Priority(int id) {
        this.id = id;
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

        Priority priority = (Priority) o;

        if (id != null ? !id.equals(priority.id) : priority.id != null) return false;
        return name != null ? name.equals(priority.name) : priority.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
