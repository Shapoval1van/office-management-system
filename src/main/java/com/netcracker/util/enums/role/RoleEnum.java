package com.netcracker.util.enums.role;

/**
 * Created by Max on 10.03.2017.
 */
public enum RoleEnum {
    ADMINISTRATOR("ROLE_ADMINISTRATOR"),
    PROJECT_MANAGER("ROLE_OFFICE MANAGER"),
    EMPLOYEE("ROLE_EMPLOYEE");

    private String name;

    RoleEnum(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
