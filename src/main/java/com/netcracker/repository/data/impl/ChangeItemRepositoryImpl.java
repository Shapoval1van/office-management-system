package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.entity.Field;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.ChangeItemRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ChangeItemRepositoryImpl extends GenericJdbcRepository<ChangeItem, Long> implements ChangeItemRepository {

    public static final String CHANGE_ITEM_ID_COLUMN = "change_item_id";
    public static final String OLD_VALUE_COLUMN = "old_value";
    public static final String NEW_VALUE_COLUMN = "new_value";
    public static final String CHANGE_GROUP_COLUMN = "change_group_id";
    public static final String FIELD_ID_COLUMN = "field_id";

    private String FIND_BY_REQUEST_ID= "SELECT * " +
            "FROM change_item AS CI " +
            "JOIN change_group AS CG ON CG.change_group_id = CI.change_group_id " +
            "WHERE CG.request_id=?";
    private String FIND_BY_GROUP_ID_FOR_DAY = "SELECT * " +
            "FROM change_item AS CI " +
            "JOIN change_group AS CG ON CG.change_group_id = CI.change_group_id " +
            "WHERE CG.request_id=? AND age(CG.created) < '1 day'";
    private String FIND_BY_GROUP_ID_FOR_MONTH = "SELECT * " +
            "FROM change_item AS CI " +
            "JOIN change_group AS CG ON CG.change_group_id = CI.change_group_id " +
            "WHERE CG.request_id=? AND age(CG.created) < '1 day'";


    public ChangeItemRepositoryImpl() {
        super(ChangeItem.TABLE_NAME, ChangeItem.ID_COLUMN);
    }

    public List<ChangeItem> findAllByChangeGroupId(Long id){
       return super.getJdbcTemplate().query(FIND_BY_REQUEST_ID,new Object[]{id}, mapRow());
    }

    public List<ChangeItem> findForDayByChangeGroupId(Long id){
        return super.getJdbcTemplate().query(FIND_BY_GROUP_ID_FOR_DAY,new Object[]{id}, mapRow());
    }

    public List<ChangeItem> findForMonthByChangeGroupId(Long id){
        return super.getJdbcTemplate().query(FIND_BY_GROUP_ID_FOR_MONTH,new Object[]{id}, mapRow());
    }


    @Override
    public Map<String, Object> mapColumns(ChangeItem entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(CHANGE_GROUP_COLUMN, entity.getId());
        columns.put(OLD_VALUE_COLUMN, entity.getOldVal());
        columns.put(NEW_VALUE_COLUMN, entity.getNewVal());
        columns.put(CHANGE_GROUP_COLUMN, entity.getChangeGroup().getId());
        columns.put(FIELD_ID_COLUMN, entity.getField().getId());
        return columns;
    }

    @Override
    public RowMapper<ChangeItem> mapRow() {
        return new RowMapper<ChangeItem>() {
            @Override
            public ChangeItem mapRow(ResultSet resultSet, int i) throws SQLException {
                ChangeItem changeItem = new ChangeItem();
                changeItem.setId(resultSet.getLong(CHANGE_ITEM_ID_COLUMN));
                changeItem.setOldVal(resultSet.getString(OLD_VALUE_COLUMN));
                changeItem.setNewVal(resultSet.getString(NEW_VALUE_COLUMN));
                changeItem.setField(new Field(resultSet.getInt(FIELD_ID_COLUMN)));
                changeItem.setChangeGroup(new ChangeGroup(resultSet.getLong(CHANGE_GROUP_COLUMN)));
                return changeItem;
            }
        };
    }
}
