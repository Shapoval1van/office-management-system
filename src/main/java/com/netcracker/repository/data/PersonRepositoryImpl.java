package com.netcracker.repository.data;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.GenericJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class PersonRepositoryImpl extends GenericJdbcRepository<Person, Long> implements PersonRepository{

    public static final String PERSON_ID_COLUMN = "person_id";
    public static final String FIRST_NAME_COLUMN = "first_name";
    public static final String LAST_NAME_COLUMN = "last_name";
    public static final String EMAIL_COLUMN = "email";
    public static final String PASSWORD_COLUMN = "password";
    public static final String ROLE_ID_COLUMN = "role_id";
    public static final String ENABLED_COLUMN = "enabled";

    private final String FIND_PERSON_BY_EMAIL = "SELECT person_id, first_name, last_name, email, password, role_id, enabled"+
            " FROM " + TABLE_NAME + " WHERE email = ?";
    private final String UPDATE_PERSON_PASSWORD = "UPDATE " + TABLE_NAME + " SET password = ? WHERE email = ?";

    public PersonRepositoryImpl() {
        super(Person.TABLE_NAME, Person.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Person entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(PERSON_ID_COLUMN, entity.getId());
        columns.put(FIRST_NAME_COLUMN, entity.getFirstName());
        columns.put(LAST_NAME_COLUMN, entity.getPassword());
        columns.put(EMAIL_COLUMN, entity.getEmail());
        columns.put(PASSWORD_COLUMN, entity.getPassword());
        columns.put(ROLE_ID_COLUMN, entity.getRole().getId());
        columns.put(ENABLED_COLUMN, entity.isEnabled());
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
                return person;
            }
        };
    }

    @Override
    public Optional<Person> findPersonByEmail(String email) {
        return super.queryForObject(FIND_PERSON_BY_EMAIL, email);
    }

    @Override
    public Optional<Person> updatePersonPassword(Person person) {
        if (person.getId() != null) {
            return super.save(person);
        }else{
            return Optional.empty();
        }
    }

    @Override
    public int updatePersonPassword(String newPassword, String email) {
        return getJdbcTemplate().update(UPDATE_PERSON_PASSWORD, newPassword, email);
    }
}
