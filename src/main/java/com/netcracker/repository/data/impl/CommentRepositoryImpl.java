package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Comment;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentRepositoryImpl extends GenericJdbcRepository<Comment, Long> implements CommentRepository {

    public static final String COMMENT_ID_COLUMN = "comment_id";
    public static final String BODY = "body";
    public static final String REQUEST_ID = "request_id";
    public static final String AUTHOR_ID = "author_id";
    public static final String PUBLISH_DATE = "publish_date";

    @Value("${comment.find.by.request.id}")
    private String FIND_COMMENTS_BY_REQUEST_ID;

    @Value("${comment.count.by.request.id}")
    private String COUNT_COMMENTS_BY_REQUEST_ID;

    public CommentRepositoryImpl() {
        super(Comment.TABLE_NAME, Comment.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Comment entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(COMMENT_ID_COLUMN, entity.getId());
        columns.put(BODY, entity.getBody());
        columns.put(REQUEST_ID, entity.getRequest().getId());
        columns.put(AUTHOR_ID, entity.getAuthor().getId());
        columns.put(PUBLISH_DATE, entity.getPublishDate());
        return columns;
    }

    @Override
    public RowMapper<Comment> mapRow() {
        return new RowMapper<Comment>() {
            @Override
            public Comment mapRow(ResultSet resultSet, int i) throws SQLException {
                Comment comment = new Comment();
                comment.setId(resultSet.getLong(COMMENT_ID_COLUMN));
                comment.setBody(resultSet.getString(BODY));
                comment.setAuthor(new Person(resultSet.getLong(AUTHOR_ID)));
                comment.setRequest(new Request(resultSet.getLong(REQUEST_ID)));
                comment.setPublishDate(new Date(resultSet.getTimestamp(PUBLISH_DATE).getTime()));
                return comment;
            }
        };
    }

    @Override
    public List<Comment> findCommentByRequestId(Long requestId) {
        List<Comment> requestComments = super.queryForList(FIND_COMMENTS_BY_REQUEST_ID, requestId);
        return requestComments;
    }

    @Override
    public List<Comment> findCommentByRequestId(Long requestId, Pageable pageable) {
        List<Comment> requestComments = super.queryForList(FIND_COMMENTS_BY_REQUEST_ID, pageable, requestId);
        return requestComments;
    }

    @Override
    public Long countCommentByRequest(Long requestId) {
        return super.jdbcTemplate.queryForObject(COUNT_COMMENTS_BY_REQUEST_ID, Long.class, requestId);
    }

}