package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;
import org.springframework.security.core.GrantedAuthority;

public class Role implements Persistable<Integer>, GrantedAuthority {

    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

    public static final String TABLE_NAME = "ROLE";
    public static final String ID_COLUMN = "role_id";

    private Integer id;
    private String name;

    public Role() {
    }

    public Role(Integer id) {
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
    public String getAuthority() {
        return name;
    }
}
