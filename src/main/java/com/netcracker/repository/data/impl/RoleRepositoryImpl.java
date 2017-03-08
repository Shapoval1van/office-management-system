package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RoleRepositoryImpl extends GenericJdbcRepository<Role, Integer> implements RoleRepository {

    public static final String ROLE_ID_COLUMN = "role_id";
    public static final String NAME_COLUMN = "name";

    public RoleRepositoryImpl() {
        super(Role.TABLE_NAME, Role.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Role entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(ROLE_ID_COLUMN, entity.getId());
        columns.put(NAME_COLUMN, entity.getName());
        return columns;
    }

    @Override
    public RowMapper<Role> mapRow() {
        return new RowMapper<Role>() {
            @Override
            public Role mapRow(ResultSet resultSet, int i) throws SQLException {
                Role role = new Role();
                role.setId(resultSet.getInt(ROLE_ID_COLUMN));
                role.setName(resultSet.getString(NAME_COLUMN));
                return role;
            }
        };
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        return queryForObject(this.buildFindByNameQuery(), name);
    }

    @Override
    public Optional<Role> findRoleById(int id){return super.findOne(id);}

    @Override
    public List<Role> findAllRoles(){return super.findAll();}
    private String buildFindByNameQuery(){
        return new StringBuilder("SELECT * FROM ")
                .append(this.TABLE_NAME)
                .append(" WHERE ")
                .append(NAME_COLUMN)
                .append(" = ?")
                .toString();
    }
}
