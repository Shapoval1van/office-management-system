package com.netcracker.service.comment;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.CommentRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Override
    public List<Comment> getCommentByRequestId(Long requestId) {
        return commentRepository.findCommentByRequestId(requestId);
    }

    @Override
    public List<Comment> getCommentByRequestId(Long requestId, Pageable pageable) {
        return commentRepository.findCommentByRequestId(requestId, pageable);
    }

    @Override
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findOne(commentId);
    }

    @Override
    public Optional<Comment> saveComment(Comment comment) throws ResourceNotFoundException {

        Long requestId = comment.getRequest().getId();
        Long authorId = comment.getAuthor().getId();

        if (!requestRepository.findOne(requestId).isPresent()) {
            LOGGER.error("Can't find request with id {}", requestId);
            throw new ResourceNotFoundException("Can't find request with id " + requestId);
        }

        if (!personRepository.findOne(authorId).isPresent()) {
            LOGGER.error("Can't find person with id {}", authorId);
            throw new ResourceNotFoundException("Can't find person with id " + authorId);
        }

        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> saveComment(CommentDTO commentDTO, Principal principal) throws ResourceNotFoundException {
        LOGGER.debug("Convert comment dto to comment");
        Comment comment = commentDTO.toComment();
        LOGGER.debug("Set publish date and author");
        comment.setPublishDate(new Date());

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
            comment.setAuthor(currentUser.get());

        return saveComment(comment);
    }
}
