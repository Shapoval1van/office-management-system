package com.netcracker.repository.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class contains implementation of generic CRUD operations.
 * @param <T> the domain type
 * @param <ID> the type of the id of the entity
 */
public abstract class GenericJdbcRepository<T extends Persistable<ID>, ID extends Number> implements JdbcRepository<T,ID> {

    protected JdbcTemplate jdbcTemplate;
    protected final String TABLE_NAME;
    protected final String ID_COLUMN;
    private SimpleJdbcInsert insertEntity;
    private String findOneQuery;
    private String findAllQuery;
    private String updateOneQuery;
    private String deleteOneQuery;
    private String deleteAllQuery;
    private String countQuery;


    public GenericJdbcRepository(@NotNull String TABLE_NAME,@NotNull String ID_COLUMN) {
        this.TABLE_NAME = Objects.requireNonNull(TABLE_NAME);
        this.ID_COLUMN = Objects.requireNonNull(ID_COLUMN);
    }

    public String getIdColumnName(){
        return this.ID_COLUMN;
    }

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertEntity = new SimpleJdbcInsert(dataSource)
                .withTableName(this.TABLE_NAME)
                .usingGeneratedKeyColumns(this.ID_COLUMN);
    }

    @Override
    public Optional<T> findOne(ID id) {
        try {
            T object = this.jdbcTemplate.queryForObject(this.buildFindOneQuery(), new Object[]{id}, this.mapRow());
            return Optional.ofNullable(object);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        List<T> result = this.jdbcTemplate.query(this.buildFindAllQuery(), this.mapRow());
        return result;
    }

    @Override
    public Optional<T> save(T entity) {
        if (entity.getId() != null){
            this.jdbcTemplate.update(this.buildUpdateQuery(entity), this.buildUpdateValues(entity));
            return Optional.ofNullable(entity);
        } else {
            ID id = (ID) insertEntity.executeAndReturnKey(this.mapColumns(entity));
            entity.setId(id);
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public void delete(ID id) {
        this.jdbcTemplate.update(this.buildDeleteOneQuery(), id);
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update(this.buildDeleteAllQuery());
    }

    @Override
    public long count() {
        return this.jdbcTemplate.queryForObject(this.buildCountQuery(), Integer.class);
    }

    @Override
    public Optional<T> queryForObject(String sql, Object... args) {
        try {
            T object = this.jdbcTemplate.queryForObject(sql, args, this.mapRow());
            return Optional.ofNullable(object);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<T> queryForList(String sql, Object... args) {
        return this.jdbcTemplate.query(sql, args, this.mapRow());
    }

    @Override
    public List<T> findAll(Pageable pageable) {
        if (pageable!=null){
            return this.jdbcTemplate.query(this.buildPageableQuery(this.buildFindAllQuery(), pageable), this.mapRow());
        } else {
            return this.findAll();
        }
    }

    @Override
    public List<T> queryForList(String sql, Pageable pageable, Object... args) {
        if (pageable!=null){
            if(pageable.getSort()!=null) {
                ArrayList<String> sotField = getValidSortStrings(pageable.getSort());
                if (sotField.size() != 0) {
                    return this.jdbcTemplate.query(this.buildPageableQueryWithSort(sql, pageable, sotField), args, this.mapRow());
                }
            }
            return this.jdbcTemplate.query(this.buildPageableQuery(sql, pageable), args, this.mapRow());
        } else {
            return this.queryForList(sql, args);
        }
    }

    protected JdbcTemplate getJdbcTemplate(){
        return jdbcTemplate;
    }

    private String buildFindOneQuery(){
        if (findOneQuery == null){
            findOneQuery = new StringBuilder("SELECT * FROM ")
                    .append(this.TABLE_NAME)
                    .append(" WHERE ")
                    .append(this.ID_COLUMN)
                    .append(" = ?").toString();
        }
        return findOneQuery;
    }

    private String buildFindAllQuery(){
        if (findAllQuery == null){
            findAllQuery = new StringBuilder("SELECT * FROM ")
                    .append(this.TABLE_NAME).toString();
        }
        return findAllQuery;
    }

    private String buildDeleteOneQuery(){
        if (deleteOneQuery == null){
            deleteOneQuery = new StringBuilder("DELETE FROM ")
                    .append(this.TABLE_NAME)
                    .append(" WHERE ")
                    .append(this.ID_COLUMN).append(" = ?").toString();
        }
        return deleteOneQuery;
    }

    private String buildDeleteAllQuery(){
        if (deleteAllQuery == null){
            deleteAllQuery = new StringBuilder("DELETE FROM ")
                    .append(this.TABLE_NAME).toString();
        }
        return deleteAllQuery;
    }

    private String buildCountQuery(){
        if (countQuery == null){
            countQuery = new StringBuilder("SELECT COUNT(*) FROM ")
                    .append(this.TABLE_NAME).toString();
        }
        return countQuery;
    }


    private String buildUpdateQuery(T entity){
        if (updateOneQuery == null){
            Map<String, Object> columns = this.mapColumns(entity);
            StringBuilder sql = new StringBuilder("UPDATE ")
                    .append(this.TABLE_NAME)
                    .append(" SET ");
            columns.keySet().stream().forEachOrdered(key -> sql.append(key).append(" = ?, "));
            updateOneQuery = sql.deleteCharAt(sql.lastIndexOf(","))
                    .append(" WHERE ")
                    .append(this.ID_COLUMN)
                    .append(" = ?").toString();
        }
        return updateOneQuery;
    }

    private Object[] buildUpdateValues(T entity){
        List<Object> values = new ArrayList<>();
        Map<String, Object> columns = this.mapColumns(entity);
        columns.keySet().stream().forEachOrdered(key -> values.add(columns.get(key)));
        values.add(entity.getId());
        return values.toArray();
    }

    protected String buildPageableQuery(String sql, Pageable pageable){
        Objects.requireNonNull(pageable);
        StringBuilder query = new StringBuilder(sql);
        if (!sql.toLowerCase().contains("order by")){
            query.append(" ORDER BY ").append(ID_COLUMN);
        }
        return query.append(" LIMIT ")
                .append(pageable.getPageSize())
                .append(" OFFSET ")
                .append(pageable.getPageSize()*pageable.getPageNumber()).toString();
    }

    private String buildPageableQueryWithSort(String sql, Pageable pageable, ArrayList<String> sortFields){
        Objects.requireNonNull(pageable);
        StringBuilder query = new StringBuilder(sql);
        if (!sql.toLowerCase().contains("order by")){
            query.append(" ORDER BY ");
            sortFields.forEach(field->
                query.append(getSortField(field))
                        .append(" ")
                        .append(getSortOrder(field))
                        .append(", "));
            query.deleteCharAt(query.lastIndexOf(","));
        }
        return query.append(" LIMIT ")
                .append(pageable.getPageSize())
                .append(" OFFSET ")
                .append(pageable.getPageSize()*pageable.getPageNumber()).toString();
    }


    private boolean isSortFieldValid(String sort){
        String  query = "SELECT count(*) FROM  INFORMATION_SCHEMA.COLUMNS WHERE table_name = ? AND column_name = ?";
        if(!isSortSubStringValid(sort)) return false;
        String fieldName = getSortField(sort);
        Object[] args = new Object[]{this.TABLE_NAME.toLowerCase(), fieldName.toLowerCase()};
        Integer count = this.jdbcTemplate.queryForObject(query, args, Integer.class );
        return count == 1;
    }

    private boolean isSortSubStringValid(String sort){
        Pattern p = Pattern.compile("([-])?\\w+_?\\w+");
        Matcher m = p.matcher(sort);
        return m.matches();
    }

    private String getSortField(String sort){
        if(sort.contains("-")){
           return sort.substring(1,sort.length());
        }else {
           return sort;
        }
    }

    private String getSortOrder(String sort){
        if(sort.contains("-")){
            return  "DESC";
        }else {
            return "ASC";
        }
    }

    private  ArrayList<String> getValidSortStrings(String sort){
        String[] sortFields = sort.split(",");
        ArrayList<String> result =  new ArrayList<>();
        for (String sortField : sortFields) {
            if (isSortFieldValid(sortField)) result.add(sortField);
        }
        return result;
    }

    public abstract  Map<String, Object> mapColumns(T entity);
    public abstract RowMapper<T> mapRow();
}
