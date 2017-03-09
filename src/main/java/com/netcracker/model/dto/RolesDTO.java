package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrey on 04.03.2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolesDTO {

    @JsonProperty("roles")
    private List<RoleDTO> roles = new ArrayList<>();

    public RolesDTO(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
