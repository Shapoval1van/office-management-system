package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.comment.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(CommentController.STATIC_MAPPING)
public class CommentController {

    public static final String STATIC_MAPPING = "/api/comment";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);
    private static final String COMMENT_DESTINATION = "/topic/request/";

    @Autowired
    private CommentService commentService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @JsonView(View.Public.class)
    @PostMapping("/")
    @MessageMapping("/chat")
    public void addAndSendComment(@Validated(CreateValidatorGroup.class) @RequestBody CommentDTO commentDTO,
                                  Principal principal) throws ResourceNotFoundException {
        Comment comment = commentService.saveComment(commentDTO, principal).get();
        simpMessagingTemplate.convertAndSend(COMMENT_DESTINATION + commentDTO.getRequest(), comment);
    }


    @GetMapping("/request/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<Comment> getCommentsOfRequest(@PathVariable("requestId") Long requestId, Pageable pageable) {
        LOGGER.debug("Get {} page of comment of {} request. Page size {}", pageable.getPageNumber(), requestId, pageable.getPageSize());
        return commentService.getCommentByRequestId(requestId, pageable);
    }
}
