package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Role;
import com.netcracker.model.view.View;

import java.util.List;

/**
 * Created by andrey on 04.03.2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDTO {
    @JsonView(View.Public.class)
    private Integer id;

    @JsonView(View.Public.class)
    private String name;

    public RoleDTO(Role role){
        this.id = role.getId();
        this.name = role.getName();
    }
    public Role toRole(){
        Role role = new Role();
        role.setId(this.id);
        role.setName(this.name);
        return role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
