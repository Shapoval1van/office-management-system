package com.netcracker.service.sub;

import com.netcracker.model.entity.Priority;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.interfaces.PriorityRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubRequestServiceImpl {

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private PriorityRepository priorityRepository;

    public List<Status> getStatuses(){
        return statusRepository.findAll();
    }

    public List<Priority> getPriorities(){
        return priorityRepository.findAll();
    }
}
