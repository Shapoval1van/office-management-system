package com.netcracker.controller;

import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.entity.Comment;
import com.netcracker.repository.data.interfaces.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@Controller
@RequestMapping(CommentController.STATIC_MAPPING)
public class CommentController {

    public static final String STATIC_MAPPING = "/api/comment";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/")
    @MessageMapping("/chat")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendMessage(@RequestBody CommentDTO commentDTO) {
        commentDTO.setPublishDate(new Date());
        Comment comment = commentDTO.toComment();
        commentRepository.save(comment);
        simpMessagingTemplate.convertAndSend("/topic/request/" + commentDTO.getRequest(), comment);
    }
}
