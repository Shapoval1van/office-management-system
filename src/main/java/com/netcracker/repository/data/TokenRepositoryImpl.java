package com.netcracker.repository.data;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;
import com.netcracker.repository.common.GenericJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class TokenRepositoryImpl extends GenericJdbcRepository<Token, Long> implements TokenRepository {

    public static final String TOKEN_ID_COLUMN = "token_id";
    public static final String TOKEN_COLUMN = "token";
    public static final String PERSON_ID_COLUMN = "person_id";
    public static final String DATE_EXPIRED_COLUMN = "date_expired";

    private final String FIND_TOKEN = "SELECT token_id, token, person_id, date_expired FROM TOKEN " +
            "WHERE token = ?";

    public TokenRepositoryImpl() {
        super(Token.TABLE_NAME, Token.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Token entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(TOKEN_ID_COLUMN, entity.getId());
        columns.put(TOKEN_COLUMN, entity.getTokenValue());
        columns.put(PERSON_ID_COLUMN, entity.getPerson().getId());
        columns.put(DATE_EXPIRED_COLUMN, entity.getDateExpired());
        return columns;
    }

    @Override
    public RowMapper<Token> mapRow() {
        return new RowMapper<Token>() {
            @Override
            public Token mapRow(ResultSet resultSet, int i) throws SQLException {
                Token token = new Token();
                token.setId(resultSet.getLong(TOKEN_ID_COLUMN));
                token.setTokenValue(resultSet.getString(TOKEN_COLUMN));
                token.setPerson(new Person(resultSet.getLong(PERSON_ID_COLUMN)));
                token.setDateExpired(resultSet.getDate(DATE_EXPIRED_COLUMN));
                return token;
            }
        };
    }

    @Override
    public Optional<Token> findTokenByPerson(Long personId) {
        return queryForObject(this.buildFindTokenByPersonQuery(), personId);
    }

    @Override
    public Optional<Token> findTokenByValueAndExpiredDate(String token) {
        return queryForObject(this.buildFindTokenByValue(), token);
    }

    @Override
    public Optional<Token> findTokenByValue(String token) {
        return queryForObject(FIND_TOKEN, token);
    }

    private String buildFindTokenByPersonQuery(){
        return new StringBuilder("SELECT * FROM ")
                .append(this.TABLE_NAME)
                .append(" WHERE ")
                .append(PERSON_ID_COLUMN)
                .append(" = ? ")
                .toString();
    }

    private String buildFindTokenByValue(){
        return new StringBuilder("SELECT * FROM ")
                .append(this.TABLE_NAME)
                .append(" WHERE ")
                .append(TOKEN_COLUMN)
                .append(" = ? ")
                .append(" AND date_expired >= now() ")
                .toString();
    }


}
