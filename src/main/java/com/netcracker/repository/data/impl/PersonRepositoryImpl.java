package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
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

    private final String FIND_PERSON_BY_EMAIL = "SELECT person_id, first_name, last_name, email, password, role_id, enabled"+
            " FROM " + TABLE_NAME + " WHERE email = ?";


    private final String FIND_MANAGER_NAME_PATTERN = "SELECT person_id, first_name, last_name, email, password, role_id, enabled " +
            "FROM  " + TABLE_NAME + " WHERE (LOWER(CONCAT(first_name, last_name)) like LOWER(CONCAT('%', REPLACE(? , ' ', '%'), '%'))) AND " +
            "  role_id = 2";

//    private final String UPDATE_USER = "UPDATE "  + TABLE_NAME + " set first_name = ?, last_name = ?, role_id = ?"+
//            " WHERE person_id = ?";

    private final String FIND_MANAGER = "SELECT person_id, first_name, last_name, email, password, role_id, enabled"+
            " FROM " + TABLE_NAME + " WHERE role_id = 2";

    private final String FIND_ADMIN = "SELECT person_id, first_name, last_name, email, password, role_id, enabled"+
            " FROM " + TABLE_NAME + " WHERE role_id = 1 AND person_id!= ?";


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
    public Optional<Person> updateUser(Person user, Long userId) {
        if (user.getId() != null) {
            return super.save(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Person> getManagers(Pageable pageable, String namePattern) {
        return super.queryForList(FIND_MANAGER_NAME_PATTERN, pageable, namePattern);
    }

    @Override
    public List<Person> getManagers(Pageable pageable) {
        return super.queryForList(FIND_MANAGER, pageable);
    }

    @Override
    public List<Person> getAdmins(Pageable pageable, Long currentAdminId) {
        return super.queryForList(FIND_ADMIN, currentAdminId);
    }
}
