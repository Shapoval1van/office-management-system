package com.netcracker.repository.data;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.VerificationToken;
import com.netcracker.repository.common.GenericJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class VerificationTokenRepositoryImpl extends GenericJdbcRepository<VerificationToken, Long> implements VerificationTokenRepository {

    public static final String TOKEN_ID_COLUMN = "verification_token_id";
    public static final String TOKEN_COLUMN = "token";
    public static final String PERSON_ID_COLUMN = "employee_id";
    public static final String DATE_EXPIRED_COLUMN = "date_expired";

    public VerificationTokenRepositoryImpl() {
        super(VerificationToken.TABLE_NAME, VerificationToken.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(VerificationToken entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(TOKEN_ID_COLUMN, entity.getId());
        columns.put(TOKEN_COLUMN, entity.getToken());
        columns.put(PERSON_ID_COLUMN, entity.getPerson().getId());
        columns.put(DATE_EXPIRED_COLUMN, entity.getDateExpired());
        return columns;
    }

    @Override
    public RowMapper<VerificationToken> mapRow() {
        return new RowMapper<VerificationToken>() {
            @Override
            public VerificationToken mapRow(ResultSet resultSet, int i) throws SQLException {
                VerificationToken verificationToken = new VerificationToken();
                verificationToken.setId(resultSet.getLong(TOKEN_ID_COLUMN));
                verificationToken.setToken(resultSet.getString(TOKEN_COLUMN));
                verificationToken.setPerson(new Person(resultSet.getLong(PERSON_ID_COLUMN)));
                verificationToken.setDateExpired(resultSet.getDate(DATE_EXPIRED_COLUMN));
                return verificationToken;
            }
        };
    }

    @Override
    public Optional<VerificationToken> findVerificationTokenByPerson(Long personId) {
        return queryForObject(this.buildFindVerificationTokenByPersonQuery(), personId);
    }

    @Override
    public Optional<VerificationToken> findVerificationTokenByValueAndExpiredDate(String token) {
        return queryForObject(this.buildFindVerificationTokenByToken(), token);
    }

    private String buildFindVerificationTokenByPersonQuery(){
        return new StringBuilder("SELECT * FROM ")
                .append(this.TABLE_NAME)
                .append(" WHERE ")
                .append(PERSON_ID_COLUMN)
                .append(" = ? ")
                .toString();
    }

    private String buildFindVerificationTokenByToken(){
        return new StringBuilder("SELECT * FROM ")
                .append(this.TABLE_NAME)
                .append(" WHERE ")
                .append(TOKEN_COLUMN)
                .append(" = ? ")
                .append(" AND date_expired >= now() ")
                .toString();
    }


}
