package com.netcracker.model.dto;


import com.fasterxml.jackson.annotation.JsonView;

import com.netcracker.model.entity.Person;
import com.netcracker.model.view.View;


public class FullPersonDTO {

    @JsonView(View.Public.class)
    private Long id;

    @JsonView(View.Public.class)
    private String firstName;

    @JsonView(View.Public.class)
    private String lastName;

    @JsonView(View.Public.class)
    private String email;

    @JsonView(View.Public.class)
    private String password;

    @JsonView(View.Public.class)
    private RoleDTO role;

    @JsonView(View.Public.class)
    private Boolean enabled;

    public FullPersonDTO(Person person){
        if (person != null) {
            this.id = person.getId();
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.email = person.getEmail();
            if (person.getRole().getId()!=null)
                this.role = new RoleDTO(person.getRole());
            this.enabled = person.isEnabled();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
