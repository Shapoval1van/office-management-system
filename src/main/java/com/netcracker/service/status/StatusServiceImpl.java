package com.netcracker.service.status;


import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.interfaces.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatusServiceImpl implements StatusService{

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public Optional<Status> getStatusById(Integer id) {
        return this.statusRepository.findOne(id) ;
    }
}
