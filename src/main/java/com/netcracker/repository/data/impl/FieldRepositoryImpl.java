package com.netcracker.repository.data.impl;


import com.netcracker.model.entity.Field;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.FieldRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class FieldRepositoryImpl extends GenericJdbcRepository<Field, Integer> implements FieldRepository{

    public static final String ID_COLUMN = "field_id";
    public static final String NAME_COLUMN = "name";

    @Value("${field.select.all}")
    private String FIND_BY_NAME;

    public FieldRepositoryImpl() {
        super(Field.TABLE_NAME, Field.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Field entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(ID_COLUMN, entity.getId());
        columns.put(NAME_COLUMN, entity.getName());
        return columns;
    }

    @Override
    public RowMapper<Field> mapRow() {
        return (resultSet, i) -> {
            Field field = new Field();
            field.setId(resultSet.getInt(ID_COLUMN));
            field.setName(resultSet.getString(NAME_COLUMN));
            return field;
        };
    }

    @Override
    public Optional<Field> findFieldByName(String name) {
        return super.queryForObject(FIND_BY_NAME, name);
    }
}
