package com.netcracker.service.comment;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.CommentDTO;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.Comment;
import com.netcracker.repository.common.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> getCommentByRequestId(Long requestId);

    Page<Comment> getCommentByRequestId(Long requestId, Pageable pageable);

    Optional<Comment> getCommentById(Long commentId);

    Optional<Comment> saveComment(Comment comment) throws ResourceNotFoundException;

    Optional<Comment> saveComment(CommentDTO commentDTO, Principal principal) throws ResourceNotFoundException;

}
