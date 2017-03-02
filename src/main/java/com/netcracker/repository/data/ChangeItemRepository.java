package com.netcracker.repository.data;


import com.netcracker.model.entity.ChangeItem;
import com.netcracker.repository.common.JdbcRepository;

import java.util.List;

public interface ChangeItemRepository extends JdbcRepository<ChangeItem, Long> {
    public List<ChangeItem> findAllByChangeGroupId(Long id);
}
