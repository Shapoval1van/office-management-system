package com.netcracker.util.enums.role;

public enum RoleEnum{
    ADMINISTRATOR(1, "ROLE_ADMINISTRATOR"),
    PROJECT_MANAGER(2, "ROLE_OFFICE MANAGER"),
    EMPLOYEE(3, "ROLE_EMPLOYEE");

    private Integer id;
    private String name;


    RoleEnum(Integer id, String name) {
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
