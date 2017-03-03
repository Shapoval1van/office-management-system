package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.view.View;
import com.netcracker.service.comment.CommentService;
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

    @Autowired
    private CommentService commentService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @JsonView(View.Public.class)
    @PostMapping("/")
    @MessageMapping("/chat")
    public void addAndSendComment(@RequestBody CommentDTO commentDTO, Principal principal) throws CurrentUserNotPresentException {
        Comment comment = commentService.saveComment(commentDTO, principal).get();
        simpMessagingTemplate.convertAndSend("/topic/request/" + commentDTO.getRequest(), comment);
    }

    @GetMapping("/request/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Comment> getCommentsOfRequest(@PathVariable("requestId") Long requestId) {
        return commentService.getCommentByRequestId(requestId);
    }
}
