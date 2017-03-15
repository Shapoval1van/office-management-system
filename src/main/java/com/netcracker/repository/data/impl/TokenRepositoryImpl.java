package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;
import com.netcracker.model.entity.TokenType;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
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
    public static final String TOKEN_TYPE_COLUM = "token_type";

    @Value("${token.find}")
    private String FIND_TOKEN;

    @Value("${token.registration.find.by.person}")
    private String FIND_REGISTRATION_TOKEN_BY_PERSON;

    @Value("${token.reset.pass.find.by.person}")
    private String FIND_RESET_PASS_TOKEN_BY_PERSON;


    public TokenRepositoryImpl() {
        super(Token.TABLE_NAME, Token.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Token entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(TOKEN_ID_COLUMN, entity.getId());
        columns.put(TOKEN_COLUMN, entity.getTokenValue());
        columns.put(PERSON_ID_COLUMN, entity.getPerson().getId());
        columns.put(TOKEN_TYPE_COLUM, entity.getTokenType().ordinal()+1);
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
                token.setTokenType(TokenType.values()[resultSet.getInt(TOKEN_TYPE_COLUM)-1]);
                return token;
            }
        };
    }

    @Override
    public Optional<Token> findRegistrationTokenByPerson(Long personId) {
        return this.queryForObject(FIND_REGISTRATION_TOKEN_BY_PERSON, personId);
    }

    @Override
    public Optional<Token> findResetPassTokenByPerson(Long personId) {
        return this.queryForObject(FIND_RESET_PASS_TOKEN_BY_PERSON, personId);
    }

    @Override
    public Optional<Token> findTokenByValueAndExpiredDate(String token) {
        return queryForObject(this.buildFindTokenByValue(), token);
    }

    @Override
    public Optional<Token> findTokenByValue(String token) {
        return queryForObject(FIND_TOKEN, token);
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
