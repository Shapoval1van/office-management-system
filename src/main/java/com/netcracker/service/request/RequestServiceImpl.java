package com.netcracker.service.request;

import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.CannotDeleteRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService{

    @Autowired
    private RequestRepository requestRepository;

    @Override
    public Optional<Request> getRequestById(Long id) {
        return requestRepository.getRequestById(id);
    }

    @Override
    public Optional<Request> saveSubRequest(Request subRequest, Request parentRequest) throws CannotCreateSubRequestException{
        return this.requestRepository.saveSubRequest(subRequest, parentRequest);
    }

    @Override
    public Optional<Request> saveRequest(Request request) {
            return this.requestRepository.save(request);
    }

    @Override
    public Optional<Request> updateRequest(Request request) {
        return this.requestRepository.updateRequest(request);

    }

    @Override
    public List<Request> getAllSubRequest(Request parentRequest) {
        return requestRepository.getAllSubRequest(parentRequest);
    }

    @Transactional
    @Override
    public void deleteRequestById(Long id) throws CannotDeleteRequestException, ResourceNotFoundException {
        Request request = getRequestById(id).get();
        if (request.getParent() != null) {
            this.requestRepository.delete(id);
        } else {
            if (!request.getStatus().getId().equals(3)) {  // if request not closed
                changeRequestStatus(request, new Status(5));
                List<Request> subRequestList = getAllSubRequest(request);
                if (!subRequestList.isEmpty()){
                    for (Request r : subRequestList){
                        this.requestRepository.delete(r.getId());
                    }
                }
            } else throw new CannotDeleteRequestException("You cannot delete closed request");
        }
    }

    @Override
    public int changeRequestStatus(Request request, Status status) {
        return requestRepository.changeRequestStatus(request, status);
    }

}
