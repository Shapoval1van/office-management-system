package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.*;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.ChangeGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ChangeGroupRepositoryImpl extends GenericJdbcRepository<ChangeGroup, Long> implements ChangeGroupRepository{

    public static final String CHANGE_GROUP_COLUMN = "change_group_id";
    public static final String CREATED_COLUMN = "created";
    public static final String AUTHOR_ID_COLUMN = "author_id";
    public static final String REQUEST_ID_COLUMN = "request_id";

    private String FIND_BY_REQUEST_ID= "SELECT CG.request_id, CG.change_group_id, CG.created, CI.change_item_id, CI.field_id, CI.new_value, CI.old_value, " +
            "CG.author_id, P.first_name, P.last_name " +
            "FROM change_group AS CG " +
            "LEFT JOIN change_item AS CI ON CG.change_group_id = CI.change_group_id " +
            "JOIN person AS P ON CG.author_id = p.person_id "+
            "WHERE CG.request_id= ? ";
    private String PERIOD_DAY= "AND age(CG.created) < '1 day'";
    private String PERIOD_MONTH= "AND age(CG.created) < '30 day'";

    @Autowired
    private ChangeItemRepositoryImpl changeItemRepository;

    public ChangeGroupRepositoryImpl() {
        super(ChangeGroup.TABLE_NAME, ChangeGroup.ID_COLUMN);
    }

    public Set<ChangeGroup> findByRequestIdWithDetails(Long id, Period period){
        switch (period){
            case DAY:
                return super.getJdbcTemplate().query(FIND_BY_REQUEST_ID.concat(PERIOD_DAY), new Object[]{id}, resultSetExtractor());
            case MONTH:
                return super.getJdbcTemplate().query(FIND_BY_REQUEST_ID.concat(PERIOD_MONTH), new Object[]{id},resultSetExtractor());
            default:
                return super.getJdbcTemplate().query(FIND_BY_REQUEST_ID,new Object[]{id},resultSetExtractor());
        }
    }

    @Override
    public Map<String, Object> mapColumns(ChangeGroup entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(CHANGE_GROUP_COLUMN, entity.getId());
        columns.put(CREATED_COLUMN, entity.getCreateDate());
        columns.put(AUTHOR_ID_COLUMN, entity.getAuthor().getId());
        columns.put(REQUEST_ID_COLUMN, entity.getRequest().getId());
        return columns;
    }

    @Override
    public RowMapper<ChangeGroup> mapRow() {
        return new RowMapper<ChangeGroup>() {
            @Override
            public ChangeGroup mapRow(ResultSet resultSet, int i) throws SQLException {
                ChangeGroup changeGroup = new ChangeGroup();
                changeGroup.setId(resultSet.getLong(CHANGE_GROUP_COLUMN));
                changeGroup.setCreateDate(resultSet.getDate(CREATED_COLUMN));
                changeGroup.setAuthor(new Person(resultSet.getLong(AUTHOR_ID_COLUMN)));
                changeGroup.setRequest(new Request(resultSet.getLong(REQUEST_ID_COLUMN)));
                return changeGroup;
            }
        };
    }

    private ResultSetExtractor<Set<ChangeGroup>> resultSetExtractor(){
        return new ResultSetExtractor<Set<ChangeGroup>>() {
            @Override
            public Set<ChangeGroup> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<Long, ChangeGroup>  changeGroupMap = new HashMap();
                while (resultSet.next()){
                    Long changeGroupId = resultSet.getLong(ChangeGroup.ID_COLUMN);
                    ChangeGroup changeGroup = changeGroupMap.get(changeGroupId);
                    if(changeGroup==null){
                        changeGroup = mapRow().mapRow(resultSet,0);
                        Person person = new Person();
                        person.setId(resultSet.getLong(AUTHOR_ID_COLUMN));
                        person.setFirstName(resultSet.getString(PersonRepositoryImpl.FIRST_NAME_COLUMN));
                        person.setLastName(resultSet.getString(PersonRepositoryImpl.LAST_NAME_COLUMN));
                        changeGroup.setAuthor(person);
                        changeGroup.setChangeItems(new HashSet<>());
                        changeGroupMap.put(changeGroupId, changeGroup);
                    }
                    ChangeItem changeItem = changeItemRepository.mapRow().mapRow(resultSet, 0);
                    changeGroup.getChangeItems().add(changeItem);
                }
                return new HashSet<>(changeGroupMap.values());
            }
        };
    }
}
