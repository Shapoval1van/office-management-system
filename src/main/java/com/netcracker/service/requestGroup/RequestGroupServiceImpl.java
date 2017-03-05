package com.netcracker.service.requestGroup;

import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.RequestGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestGroupServiceImpl implements RequestGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGroupServiceImpl.class);

    @Autowired
    private RequestGroupRepository requestGroupRepository;


    @Override
    public List<RequestGroup> getRequestGroupByAuthorId(Long authorId, Pageable pageable) {
        return requestGroupRepository.findRequestGroupByAuthorId(authorId, pageable);
    }

    @Override
    public List<RequestGroup> getRequestGroupByNamePart(String namePart, Pageable pageable) {
        String regex = generateRegexByNamePart(namePart);
        return requestGroupRepository.findRequestGroupByNameRegex(regex, pageable);
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
