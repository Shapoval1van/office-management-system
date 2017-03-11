package com.netcracker.repository.data.interfaces;


import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.model.entity.Period;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.util.Set;

public interface ChangeGroupRepository extends JdbcRepository<ChangeGroup, Long> {
    public Set<ChangeGroup> findByRequestIdWithDetails(Long id, Period period, Pageable pageable);
}
