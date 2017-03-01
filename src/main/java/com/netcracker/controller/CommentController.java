package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.view.View;
import com.netcracker.repository.data.interfaces.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(CommentController.STATIC_MAPPING)
public class CommentController {

    public static final String STATIC_MAPPING = "/api/comment";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @JsonView(View.Public.class)
    @PostMapping("/")
    @MessageMapping("/chat")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAndSendComment(@RequestBody CommentDTO commentDTO) {
        commentDTO.setPublishDate(new Date());
        Comment comment = commentDTO.toComment();
        commentRepository.save(comment);
        simpMessagingTemplate.convertAndSend("/topic/request/" + commentDTO.getRequest(), commentDTO);
    }

    @GetMapping("/request/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Comment> getCommentsOfRequest(@PathVariable("requestId") Long requestId) {
        return commentRepository.findCommentByRequestId(requestId);
    }
}
