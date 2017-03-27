package com.netcracker.service.comment;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.entity.Person;
import com.netcracker.model.event.RequestNewCommentEvent;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.CommentRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.netcracker.util.MessageConstant.*;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    public CommentServiceImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Comment> getCommentByRequestId(Long requestId) {
        return commentRepository.findCommentByRequestId(requestId);
    }

    @Override
    public Page<Comment> getCommentByRequestId(Long requestId, Pageable pageable) {
        List<Comment> commentByRequest = commentRepository.findCommentByRequestId(requestId, pageable);
        Long count = commentRepository.countCommentByRequest(requestId);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, commentByRequest);
    }

    @Override
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findOne(commentId);
    }

    @Override
    public Optional<Comment> saveComment(Comment comment) throws ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        Long requestId = comment.getRequest().getId();
        Long authorId = comment.getAuthor().getId();

        if (!requestRepository.findOne(requestId).isPresent()) {
            LOGGER.error("Can't find request with id {}", requestId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
        }

        if (!personRepository.findOne(authorId).isPresent()) {
            LOGGER.error("Can't find person with id {}", authorId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(USER_WITH_ID_NOT_PRESENT, new Object[]{authorId}, locale));
        }

        eventPublisher.publishEvent(new RequestNewCommentEvent(requestRepository.findOne(requestId).get()));
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> saveComment(CommentDTO commentDTO, Principal principal) throws ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        LOGGER.debug("Convert comment dto to comment");
        Comment comment = commentDTO.toComment();
        LOGGER.debug("Set publish date and author");
        comment.setPublishDate(new Date());

        String currentUserEmail = principal.getName();
        if (currentUserEmail.isEmpty()) {
            LOGGER.error("Current user not present");
            throw new ResourceNotFoundException(messageSource
                    .getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        }
        Optional<Person> currentUser = personRepository.findPersonByEmail(currentUserEmail);
        if (!currentUser.isPresent()) {
            LOGGER.error("Can't fetch information about current user");

            throw new ResourceNotFoundException(messageSource
                    .getMessage(USER_WITH_EMAIL_NOT_PRESENT, new Object[]{currentUserEmail}, locale));
        } else {
            comment.setAuthor(currentUser.get());
        }

        return saveComment(comment);
    }
}
