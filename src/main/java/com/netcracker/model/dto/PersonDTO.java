package com.netcracker.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.validation.DeleteUserValidatorGroup;
import com.netcracker.model.validation.UpdateValidatorGroup;
import com.netcracker.model.view.View;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDTO {

    @JsonView(View.Public.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonView(View.Public.class)
    @Size(min = 3, max = 50, groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;
    @JsonView(View.Public.class)
    @Size(max = 50, groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastName;
    @JsonView(View.Public.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Size(max = 50, groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class, DeleteUserValidatorGroup.class})
    @NotNull(groups = CreateValidatorGroup.class)
    @Pattern(regexp = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    private String email;
    @JsonView(View.Internal.class)
    @Size(max = 70, groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    //@NotNull(groups = CreateValidatorGroup.class)
    // @JsonInclude(JsonInclude.Include.NON_NULL)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,70}$", groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class, DeleteUserValidatorGroup.class})
    private String password;
    @JsonView(View.Public.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(groups = {DeleteUserValidatorGroup.class})
    private Integer role;
    @JsonView(View.Public.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean enabled;


    public PersonDTO() {
    }

    public PersonDTO(Person person) {
        if (person != null) {
            this.id = person.getId();
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.email = person.getEmail();
            this.password = person.getPassword();
            if (person.getRole() != null) {
                this.role = person.getRole().getId();
            }
            this.enabled = person.isEnabled();
        }
    }

    public Person toPerson() {
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
