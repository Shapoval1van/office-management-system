package com.netcracker.service.comment;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.entity.Comment;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> getCommentByRequestId(Long requestId);

    Optional<Comment> getCommentById(Long commentId);

    Optional<Comment> saveComment(Comment comment);

    Optional<Comment> saveComment(CommentDTO commentDTO, Principal principal) throws CurrentUserNotPresentException;

}
