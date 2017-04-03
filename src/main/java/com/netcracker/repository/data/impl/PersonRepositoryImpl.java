package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PersonRepositoryImpl extends GenericJdbcRepository<Person, Long> implements PersonRepository {

    public static final String PERSON_ID_COLUMN = "person_id";
    public static final String FIRST_NAME_COLUMN = "first_name";
    public static final String LAST_NAME_COLUMN = "last_name";
    public static final String EMAIL_COLUMN = "email";
    public static final String PASSWORD_COLUMN = "password";
    public static final String ROLE_ID_COLUMN = "role_id";
    public static final String ENABLED_COLUMN = "enabled";
    public static final String DELETED_COLUMN = "deleted";

    public static final int SUCCESS_UPDATE_CODE = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepositoryImpl.class);

    @Value("${person.find.by.email}")
    private String FIND_PERSON_BY_EMAIL;

    @Value("${person.find.by.name.pattern}")
    private String FIND_MANAGER_NAME_PATTERN;

    @Value("${person.find.user.by.name.pattern}")
    private String FIND_USER_BY_NAME_PATTERN;

    @Value("${person.find.manager}")
    private String FIND_MANAGER;

    @Value("${person.update.password}")
    private String UPDATE_PERSON_PASSWORD;

    @Value("${person.update}")
    private String UPDATE_PERSON;

    @Value("${person.update.delete.enable}")
    private String UPDATE_PERSON_AVAILABLE;

    @Value("${person.find.all.available.by.role}")
    private String GET_AVAILABLE_PERSONS_BY_ROLE;

    @Value("${person.find.all.available}")
    private String GET_AVAILABLE_PERSONS;

    @Value("${person.count.active}")
    private String COUNT_ACTIVE_PERSON;

    @Value("${person.count.deleted}")
    private String COUNT_DELETED_PERSON;

    @Value("${person.find.all.deleted}")
    private String GET_DELETED_PERSONS;

    @Value("${person.find.all.deleted.by.role}")
    private String GET_DELETED_PERSONS_BY_ROLE;

    @Value("${person.count.active.by.role}")
    private String COUNT_ACTIVE_PERSON_BY_ROLE;

    @Value("${subscribe}")
    private String SUBSCRIBE;

    @Value("${unsubscribe}")
    private String UNSUBSCRIBE;

    @Value("${find.subscribers.by.request}")
    private String FIND_SUBSCRIBERS_BY_REQUEST;

    @Value("${person.count.deleted.by.role}")
    private String COUNT_DELETED_PERSON_BY_ROLE;


    public PersonRepositoryImpl() {
        super(Person.TABLE_NAME, Person.ID_COLUMN);
    }


    @Override
    public Map<String, Object> mapColumns(Person entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(PERSON_ID_COLUMN, entity.getId());
        columns.put(FIRST_NAME_COLUMN, entity.getFirstName());
        columns.put(LAST_NAME_COLUMN, entity.getLastName());
        columns.put(EMAIL_COLUMN, entity.getEmail());
        columns.put(PASSWORD_COLUMN, entity.getPassword());
        columns.put(ROLE_ID_COLUMN, entity.getRole().getId());
        columns.put(ENABLED_COLUMN, entity.isEnabled());
        columns.put(DELETED_COLUMN, entity.isDeleted());
        return columns;
    }

    @Override
    public RowMapper<Person> mapRow() {
        return new RowMapper<Person>() {
            @Override
            public Person mapRow(ResultSet resultSet, int i) throws SQLException {
                Person person = new Person();
                person.setId(resultSet.getLong(PERSON_ID_COLUMN));
                person.setFirstName(resultSet.getString(FIRST_NAME_COLUMN));
                person.setLastName(resultSet.getString(LAST_NAME_COLUMN));
                person.setEmail(resultSet.getString(EMAIL_COLUMN));
                person.setPassword(resultSet.getString(PASSWORD_COLUMN));
                person.setRole(new Role(resultSet.getInt(ROLE_ID_COLUMN)));
                person.setEnabled(resultSet.getBoolean(ENABLED_COLUMN));
                person.setDeleted(resultSet.getBoolean(DELETED_COLUMN));
                return person;
            }
        };
    }

    @Override
    public Optional<Person> findPersonByEmail(String email) {
        return super.queryForObject(FIND_PERSON_BY_EMAIL, email);
    }

    @Override
    public Long getCountActivePersonByRole(Integer roleId) {
        return getJdbcTemplate().queryForObject(COUNT_ACTIVE_PERSON_BY_ROLE, Long.class, roleId);
    }

    @Override
    public Long getCountDeletedPersonByRole(Integer roleId) {
        return getJdbcTemplate().queryForObject(COUNT_DELETED_PERSON_BY_ROLE, Long.class, roleId);
    }

    @Override
    public Long getCountActivePerson() {
        return getJdbcTemplate().queryForObject(COUNT_ACTIVE_PERSON, Long.class);
    }

    @Override
    public Long getCountDeletedPerson() {
        return getJdbcTemplate().queryForObject(COUNT_DELETED_PERSON, Long.class);
    }

    @Override
    public int updatePerson(Person person) {
        return getJdbcTemplate().update(UPDATE_PERSON, person.getFirstName(), person.getLastName(), person.getRole().getId(), person.getId());
    }

    @Override
    public int updatePersonAvailable(Person person) {
        return getJdbcTemplate().update(UPDATE_PERSON_AVAILABLE, person.isEnabled(), person.isDeleted(), person.getId());
    }


    @Override
    public List<Person> getManagers(Pageable pageable, String namePattern) {
        return super.queryForList(FIND_MANAGER_NAME_PATTERN, pageable, namePattern, namePattern);
    }

    @Override
    public List<Person> getManagers(Pageable pageable) {
        return super.queryForList(FIND_MANAGER, pageable);
    }

    @Override
    public List<Person> getPersons(Integer roleId, Pageable pageable, Optional<Role> role) {
        return role.isPresent() ? this.queryForList(
                GET_AVAILABLE_PERSONS_BY_ROLE, pageable, roleId)
                : this.queryForList(GET_AVAILABLE_PERSONS, pageable);
    }

    @Override
    public List<Person> getUsersByNamePattern(Pageable pageable, String namePattern) {
        return super.queryForList(FIND_USER_BY_NAME_PATTERN, pageable, namePattern);
    }

    @Override
    public List<Person> getPersonListByRole(Integer roleId, Pageable pageable, Optional<Role> role) {
        return this.queryForList(GET_AVAILABLE_PERSONS_BY_ROLE, pageable, roleId);
    }

    @Override
    public List<Person> getDeletedPersonListByRole(Integer roleId, Pageable pageable, Optional<Role> role) {
        return this.queryForList(GET_DELETED_PERSONS_BY_ROLE, pageable, roleId);
    }

    @Override
    public List<Person> getPersonList(Pageable pageable) {
        return super.queryForList(GET_AVAILABLE_PERSONS, pageable);
    }

    @Override
    public List<Person> getDeletedPersonList(Pageable pageable) {
        return super.queryForList(GET_DELETED_PERSONS, pageable);
    }

    @Override
    public List<Person> getPersonList() {
        return super.queryForList(GET_AVAILABLE_PERSONS);
    }


    @Override
    public int subscribe(Long requestId, Long personId) {
//        Don't do like that
        List<Person> subscribers = findPersonsBySubscribingRequest(requestId);
        for (Person subscriber : subscribers) {
            if (subscriber.getId().equals(personId)) {
                LOGGER.info("Person {} already subscribing on request {}", personId, requestId);
                return SUCCESS_UPDATE_CODE;
            }
        }

        return getJdbcTemplate().update(SUBSCRIBE, requestId, personId);
    }

    @Override
    public int unsubscribe(Long requestId, Long personId) {
        return getJdbcTemplate().update(UNSUBSCRIBE, requestId, personId);
    }

    @Override
    public List<Person> findPersonsBySubscribingRequest(Long requestId) {
        return queryForList(FIND_SUBSCRIBERS_BY_REQUEST, requestId);
    }
}
