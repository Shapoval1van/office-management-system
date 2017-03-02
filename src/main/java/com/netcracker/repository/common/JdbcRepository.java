package com.netcracker.repository.common;


import java.util.List;
import java.util.Optional;

/**
 * Interface for generic CRUD operations.
 * @param <T> the domain type
 * @param <ID> the type of the id of the entity
 */
public interface JdbcRepository<T extends Persistable<ID>, ID extends Number> {
    Optional<T> findOne(ID id);
    List<T> findAll();
    List<T> findAll(Pageable pageable);
    Optional<T> save(T entity);
    void delete(ID id);
    void deleteAll();
    long count();
    Optional<T> queryForObject(String sql, Object... args);
    List<T> queryForList(String sql, Object... args);
    List<T> queryForList(String sql, Pageable pageable, Object... args);
}
