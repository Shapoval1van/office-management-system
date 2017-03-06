package com.netcracker.service.requestGroup;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.RequestGroupDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestGroupRepository;
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

    @Autowired
    private RequestGroupRepository requestGroupRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<RequestGroup> getRequestGroupByAuthorId(Long authorId, Pageable pageable) {
        return requestGroupRepository.findRequestGroupByAuthorId(authorId, pageable);
    }

    @Override
    public List<RequestGroup> getRequestGroupByNamePart(String namePart, Pageable pageable) {
        String regex = generateRegexByNamePart(namePart);
        return requestGroupRepository.findRequestGroupByNameRegex(regex, pageable);
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
    public Optional<RequestGroup> updateRequestGroup(RequestGroupDTO requestGroupDTO) throws ResourceNotFoundException {

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

        LOGGER.trace("Update request group");
        return saveRequestGroup(requestGroup);
    }

    @Override
    public int getRequestGroupCountByAuthor(Long authorId) {
        LOGGER.debug("Get request grout count by author. Author id: {}", authorId);
        return requestGroupRepository.countRequestGroupByAuthor(authorId);
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
}
