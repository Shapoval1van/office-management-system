package com.netcracker.service.request;

import com.netcracker.exception.CannotCreateRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.CannotDeleteRequestException;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findOne(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Optional<Request> saveSubRequest(Request subRequest) throws CannotCreateSubRequestException {
        if (subRequest.getParent() != null) {
            Request parentRequest = requestRepository.findOne(subRequest.getParent().getId()).orElseThrow(() ->
                    new CannotCreateSubRequestException("No parent request with id " + subRequest.getParent().getId()));

            if (parentRequest.getParent() != null) {
                throw new CannotCreateSubRequestException("Parent request is sub request");
            }

            String parentStatus = parentRequest.getStatus().getName();
            if ("CANCELED".equals(parentStatus) || "CLOSED".equals(parentStatus)) {
                throw new CannotCreateSubRequestException("Parent request is closed or canceled");
            }

            subRequest.setCreationTime(new Timestamp(System.currentTimeMillis()));
            subRequest.setEmployee(parentRequest.getEmployee());
            subRequest.setPriority(parentRequest.getPriority());
            subRequest.setStatus(statusRepository.findStatusByName("IN PROGRESS").orElseThrow(() ->
                    new CannotCreateSubRequestException("No status 'IN PROGRESS'")));
            return requestRepository.save(subRequest);
        } else {
            throw new CannotCreateSubRequestException("No parent request");
        }
    }

    @Override
    public Optional<Request> saveRequest(Request request) throws CannotCreateRequestException {
        request.setStatus(statusRepository.findStatusByName("FREE").orElseThrow(() ->
                new CannotCreateRequestException("No status 'FREE'")
        ));
        request.setCreationTime(new Timestamp(new Date().getTime()));
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
    public void deleteRequestById(Long id) throws CannotDeleteRequestException{
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
            } else {
                throw new CannotDeleteRequestException("You cannot delete closed request");
            }
        }
    }

    @Override
    public int changeRequestStatus(Request request, Status status) {
        return requestRepository.changeRequestStatus(request, status);
    }

}
