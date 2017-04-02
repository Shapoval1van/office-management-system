package com.netcracker.repository.data.interfaces;


import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.model.entity.Period;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.util.List;

public interface ChangeGroupRepository extends JdbcRepository<ChangeGroup, Long> {
    List<ChangeGroup> findByRequestIdWithDetails(Long id, Period period, Pageable pageable);
    Long countChangeByRequestId(Long id, Period period);
}
