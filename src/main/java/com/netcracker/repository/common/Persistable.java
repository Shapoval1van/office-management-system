package com.netcracker.common.repository;

/**
 * Interface is used to mark entity.
 * @param <ID> the type of ID
 */
public interface Persistable<ID extends Number> {
    ID getId();
    void setId(ID id);
}
