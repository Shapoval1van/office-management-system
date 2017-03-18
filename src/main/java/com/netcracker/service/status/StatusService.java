package com.netcracker.service.status;


import com.netcracker.model.entity.Status;

import java.util.Optional;

public interface StatusService {

    Optional<Status> getStatusById(Integer id);
}
