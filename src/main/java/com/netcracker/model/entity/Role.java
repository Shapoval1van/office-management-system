package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;
import org.springframework.security.core.GrantedAuthority;

public class Role implements Persistable<Integer>, GrantedAuthority {

    public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";
    public static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    public static final String ROLE_OFFICE_MANAGER = "ROLE_OFFICE MANAGER";

    public static final String TABLE_NAME = "ROLE";
    public static final String ID_COLUMN = "role_id";

    private Integer id;
    private String name;

    public Role() {
    }

    public Role(Integer id) {
        this.id = id;
    }

    public Role(String name) {
        this.name = name;
    }

    public Role(Role role) {
        if(role == null) return;
        id = role.id;
        name = role.name;
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

        Role role = (Role) o;

        if (id != null ? !id.equals(role.id) : role.id != null) return false;
        return name != null ? name.equals(role.name) : role.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }


    @Override
    public String getAuthority() {
        return name;
    }
}
