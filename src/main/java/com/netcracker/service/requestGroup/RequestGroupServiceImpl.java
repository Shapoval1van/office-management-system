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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.netcracker.util.MessageConstant.*;

@Service
public class RequestGroupServiceImpl implements RequestGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGroupServiceImpl.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RequestGroupRepository requestGroupRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RoleRepository roleRepository;

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
        Locale locale = LocaleContextHolder.getLocale();

        LOGGER.trace("Convert dto to entity");
        RequestGroup requestGroup = requestGroupDTO.toRequestGroup();

        String currentUserEmail = principal.getName();
        if (currentUserEmail.isEmpty()) {
            LOGGER.error("Current user not present");
            throw new CurrentUserNotPresentException(messageSource
                    .getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        }
        Optional<Person> currentUser = personRepository.findPersonByEmail(currentUserEmail);
        if (!currentUser.isPresent()) {
            LOGGER.error("Can't fetch information about current user");
            throw new CurrentUserNotPresentException(messageSource
                    .getMessage(USER_WITH_EMAIL_NOT_PRESENT, new Object[]{currentUserEmail}, locale));
        } else
            requestGroup.setAuthor(currentUser.get());

        return saveRequestGroup(requestGroup);
    }

    @Override
    public Optional<RequestGroup> updateRequestGroup(RequestGroupDTO requestGroupDTO, Principal principal) throws ResourceNotFoundException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();

        LOGGER.debug("Get request group with id {} from database", requestGroupDTO.getId());
        Optional<RequestGroup> requestGroupOptional = requestGroupRepository.findOne(requestGroupDTO.getId());

        if (!requestGroupOptional.isPresent()) {
            LOGGER.error("Request group with id {} not exist", requestGroupDTO.getId());
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_GROUP_NOT_EXIST, new Object[]{requestGroupDTO.getId()}, locale));
        }

        RequestGroup requestGroup = requestGroupOptional.get();
        if (requestGroupDTO.getName() != null) {
            LOGGER.trace("Change request group name from {} to {}", requestGroup.getName(), requestGroupDTO.getName());
            requestGroup.setName(requestGroupDTO.getName());
        }

        if (!isAccessLegal(requestGroup, principal))
            throw new IllegalAccessException(messageSource
                    .getMessage(REQUEST_GROUP_ILLEGAL_ACCESS, null, locale));

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
        Locale locale = LocaleContextHolder.getLocale();

        LOGGER.trace("Getting request group with id {} from database", requestGroupId);
        Optional<RequestGroup> requestGroupOption = requestGroupRepository.findOne(requestGroupId);

        if (!requestGroupOption.isPresent()) {
            LOGGER.error("Request group with id {} does not exist", requestGroupId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestGroupId}, locale));
        }

        if (!isAccessLegal(requestGroupOption.get(), principal))
            throw new IllegalAccessException(messageSource
                    .getMessage(REQUEST_GROUP_ILLEGAL_ACCESS, null, locale));


        LOGGER.trace("Get status with id {} from database", statusId);
        Optional<Status> statusOptional = statusRepository.findOne(statusId);

        if (!statusOptional.isPresent()) {
            LOGGER.error("Status with id {} not found", statusId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(STATUS_ERROR, new Object[]{statusId}, locale));
        }

        Status status = statusOptional.get();
        String statusName = status.getName();

        if (statusName.equalsIgnoreCase(StatusEnum.FREE.getName()) ||
                statusName.equalsIgnoreCase(StatusEnum.CANCELED.getName())) {
            LOGGER.error("Can't set status {} for group", statusName);
            throw new IncorrectStatusException(messageSource.getMessage(STATUS_ERROR_NOT_AVAILABLE, null, locale),
                    messageSource.getMessage(STATUS_ERROR_NOT_AVAILABLE_FOR_GROUP, new Object[]{statusName}, locale));
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
        Locale locale = LocaleContextHolder.getLocale();

        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());
        if (!currentUser.isPresent()) {
            LOGGER.warn("Current user not present");
            throw new CurrentUserNotPresentException(messageSource
                    .getMessage(USER_ERROR_NOT_PRESENT, null, locale));
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
