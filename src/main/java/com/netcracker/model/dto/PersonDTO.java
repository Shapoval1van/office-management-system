package com.netcracker.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDTO {

    @JsonView(View.Public.class)
    private Long id;
    @JsonView(View.Public.class)
    private String firstName;
    @JsonView(View.Public.class)
    private String lastName;
    @JsonView(View.Public.class)
    @NotNull(groups = CreateValidatorGroup.class)
    private String email;
    @JsonView(View.Internal.class)
    @NotNull(groups = CreateValidatorGroup.class)
    private String password;
    @JsonView(View.Public.class)
    private Integer role;
    @JsonView(View.Public.class)
    private boolean enabled;

    public PersonDTO() {
    }

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.email = person.getEmail();
        this.password = person.getPassword();
        if (person.getRole()!=null){
            this.role = person.getRole().getId();
        }
        this.enabled = person.isEnabled();
    }

    public Person toPerson(){
        Person person = new Person();
        person.setId(this.id);
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        person.setEmail(this.email);
        person.setPassword(this.password);
        person.setRole(new Role(this.role));
        person.setEnabled(this.enabled);
        return person;
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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
