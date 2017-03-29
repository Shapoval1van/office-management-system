package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;

public class ChangeItem implements Persistable<Long> {
    public static final String TABLE_NAME = "CHANGE_ITEM";
    public static final String ID_COLUMN = "change_item_id";

    private Long id;
    private String oldVal;
    private String newVal;
    private ChangeGroup changeGroup;
    private Field field;

    public ChangeItem() {
    }

    public ChangeItem(String oldVal, String newVal, Field field) {
        this.oldVal = oldVal;
        this.newVal = newVal;
        this.field = field;
    }

    public ChangeItem(Long id) {
        this.id = id;
    }

    public String getOldVal() {
        return oldVal;
    }

    public void setOldVal(String oldVal) {
        this.oldVal = oldVal;
    }

    public String getNewVal() {
        return newVal;
    }

    public void setNewVal(String newVal) {
        this.newVal = newVal;
    }

    public ChangeGroup getChangeGroup() {
        return changeGroup;
    }

    public void setChangeGroup(ChangeGroup changeGroup) {
        this.changeGroup = changeGroup;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long aLong) {
        this.id = aLong;
    }
}
