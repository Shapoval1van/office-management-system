package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.Comment;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.util.List;

/**
 * Created by Max on 27.02.2017.
 */
public interface CommentRepository extends JdbcRepository<Comment, Long> {

    List<Comment> findCommentByRequestId(Long requestId);

    List<Comment> findCommentByRequestId(Long requestId, Pageable pageable);
    
    Long countCommentByRequest(Long requestId);
}
