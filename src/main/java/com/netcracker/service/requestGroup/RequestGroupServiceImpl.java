package com.netcracker.service.requestGroup;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.IncorrectStatusException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.RequestGroupDTO;
import com.netcracker.model.entity.*;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.*;
import com.netcracker.util.enums.role.RoleEnum;
import com.netcracker.util.enums.status.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class RequestGroupServiceImpl implements RequestGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGroupServiceImpl.class);

    private final RequestGroupRepository requestGroupRepository;

    private final PersonRepository personRepository;

    private final RequestRepository requestRepository;

    private final StatusRepository statusRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public RequestGroupServiceImpl(RequestGroupRepository requestGroupRepository,
                                   PersonRepository personRepository,
                                   RequestRepository requestRepository,
                                   StatusRepository statusRepository,
                                   RoleRepository roleRepository) {
        this.requestGroupRepository = requestGroupRepository;
        this.personRepository = personRepository;
        this.requestRepository = requestRepository;
        this.statusRepository = statusRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RequestGroup> getRequestGroupByAuthorId(Long authorId, Pageable pageable) {
        return requestGroupRepository.findRequestGroupByAuthorId(authorId, pageable);
    }

    @Override
    public List<RequestGroup> getRequestGroupByNamePart(String namePart, Long authorId) {
        String regex = generateRegexByNamePart(namePart);
        return requestGroupRepository.findRequestGroupByNameRegex(regex, authorId);
    }

    @Override
    public Optional<RequestGroup> saveRequestGroup(RequestGroup requestGroup) {
        LOGGER.debug("Save request group to db");
        return requestGroupRepository.save(requestGroup);
    }

    @Override
    public Optional<RequestGroup> saveRequestGroup(RequestGroupDTO requestGroupDTO, Principal principal) throws CurrentUserNotPresentException {
        LOGGER.trace("Convert dto to entity");
        RequestGroup requestGroup = requestGroupDTO.toRequestGroup();

        String currentUserEmail = principal.getName();
        if (currentUserEmail.isEmpty()) {
            LOGGER.error("Current user not present");
            throw new CurrentUserNotPresentException("Current user not present");
        }
        Optional<Person> currentUser = personRepository.findPersonByEmail(currentUserEmail);
        if (!currentUser.isPresent()) {
            LOGGER.error("Can't fetch information about current user");
            throw new CurrentUserNotPresentException("Can't fetch information about current user");
        } else
            requestGroup.setAuthor(currentUser.get());

        return saveRequestGroup(requestGroup);
    }

    @Override
    public Optional<RequestGroup> updateRequestGroup(RequestGroupDTO requestGroupDTO, Principal principal) throws ResourceNotFoundException, IllegalAccessException {

        LOGGER.debug("Get request group with id {} from database", requestGroupDTO.getId());
        Optional<RequestGroup> requestGroupOptional = requestGroupRepository.findOne(requestGroupDTO.getId());

        if (!requestGroupOptional.isPresent()) {
            LOGGER.error("Request group with id {} not exist", requestGroupDTO.getId());
            throw new ResourceNotFoundException("Request group with id" + requestGroupDTO.getId() + " not exist");
        }

        RequestGroup requestGroup = requestGroupOptional.get();
        if (requestGroupDTO.getName() != null) {
            LOGGER.trace("Change request group name from {} to {}", requestGroup.getName(), requestGroupDTO.getName());
            requestGroup.setName(requestGroupDTO.getName());
        }

        if (!isAccessLegal(requestGroup, principal))
            throw new IllegalAccessException("Update request group can only author or administrator");

        LOGGER.trace("Update request group");
        return saveRequestGroup(requestGroup);
    }

    @Override
    public int getRequestGroupCountByAuthor(Long authorId) {
        LOGGER.debug("Get request grout count by author. Author id: {}", authorId);
        return requestGroupRepository.countRequestGroupByAuthor(authorId);
    }

    @Override
    public void setRequestGroupStatus(Integer requestGroupId, Integer statusId, Principal principal)
            throws ResourceNotFoundException, IncorrectStatusException, IllegalAccessException {

        LOGGER.trace("Getting request group with id {} from database", requestGroupId);
        Optional<RequestGroup> requestGroupOption = requestGroupRepository.findOne(requestGroupId);

        if (!requestGroupOption.isPresent()) {
            LOGGER.error("Request group with id {} does not exist", requestGroupId);
            throw new ResourceNotFoundException("Can't find request group with id " + requestGroupId);
        }

        if (!isAccessLegal(requestGroupOption.get(), principal))
            throw new IllegalAccessException("Update request group can only author or administrator");


        LOGGER.trace("Get status with id {} from database", statusId);
        Optional<Status> statusOptional = statusRepository.findOne(statusId);

        if (!statusOptional.isPresent()) {
            LOGGER.error("Status with id {} not found", statusId);
            throw new ResourceNotFoundException("Status with id " + statusId + " not found");
        }

        Status status = statusOptional.get();
        String statusName = status.getName();

        if (statusName.equalsIgnoreCase(StatusEnum.FREE.getName()) ||
                statusName.equalsIgnoreCase(StatusEnum.CANCELED.getName())) {
            LOGGER.error("Can't set status {} for group", statusName);
            throw new IncorrectStatusException("Not available status", "Status " + statusName + " not available for group");
        }

        List<Request> requestsOfRequestGroup = requestRepository.findRequestsByRequestGroupId(requestGroupId);

        Integer canceledStatusId = statusRepository.findStatusByName(StatusEnum.CANCELED.getName()).get().getId();

        for (Request request :
                requestsOfRequestGroup) {

            Integer currentStatusId = request.getStatus().getId();

            if (!currentStatusId.equals(canceledStatusId)) {
                LOGGER.trace("Set status {} for request with id {}", statusId, request.getId());
                request.setStatus(status);
                LOGGER.trace("Save updated request {} to database", request.getId());
                requestRepository.save(request);
            } else
                LOGGER.trace("Request {} already canceled", request.getId());
        }

    }

    private String generateRegexByNamePart(String namePart) {
        String regex = new StringBuilder()
                .append(".*")
                .append(namePart)
                .append(".*")
                .toString();

        LOGGER.trace("Build {} regex from {} name part", regex, namePart);

        return regex;
    }

    private boolean isAccessLegal(RequestGroup requestGroup, Principal principal) throws CurrentUserNotPresentException, IllegalAccessException {
        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());
        if (!currentUser.isPresent()) {
            LOGGER.warn("Current user not present");
            throw new CurrentUserNotPresentException("Current user not present");
        } else if (!currentUser.get().getId().equals(requestGroup.getAuthor().getId())) {
            Optional<Role> adminRole = roleRepository.findRoleByName(RoleEnum.ADMINISTRATOR.getName());
            if (!currentUser.get().getRole().getId().equals(adminRole.get().getId())) {
                LOGGER.error("Access only for author or administrator");
                return false;
            }
        }

        return true;
    }
}
