package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.comment.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(CommentController.STATIC_MAPPING)
public class CommentController {

    public static final String STATIC_MAPPING = "/api/comment";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @JsonView(View.Public.class)
    @PostMapping("/")
    @MessageMapping("/chat")
    public void addAndSendComment(@RequestBody CommentDTO commentDTO, Principal principal) throws ResourceNotFoundException {
        Comment comment = commentService.saveComment(commentDTO, principal).get();
        simpMessagingTemplate.convertAndSend("/topic/request/" + commentDTO.getRequest(), comment);
    }


    @GetMapping("/request/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Comment> getCommentsOfRequest(@PathVariable("requestId") Long requestId, Pageable pageable) {
        LOGGER.debug("Get {} page of comment of {} request. Page size {}", pageable.getPageNumber(), requestId, pageable.getPageSize());
        return commentService.getCommentByRequestId(requestId, pageable);
    }
}
