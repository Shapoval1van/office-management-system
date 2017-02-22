package com.netcracker.repository.data;


import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.GenericJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
public class PasswordResetTokenRepositoryImpl extends GenericJdbcRepository<PasswordResetToken,Long>
        implements PasswordResetTokenRepository {

    private final String FIND_TOKEN_PERSON_BY_ID = "SELECT pass_reset_token_id, token, person_id, date_expired FROM PASS_RESET_TOKEN " +
            "WHERE person_id = ?";
    private final String FIND_TOKEN = "SELECT pass_reset_token_id, token, person_id, date_expired FROM PASS_RESET_TOKEN " +
            "WHERE token = ?";
    public PasswordResetTokenRepositoryImpl() {
        super(PasswordResetToken.TABLE_NAME, PasswordResetToken.ID_COLUMN);
    }


    @Override
    public Map<String, Object> mapColumns(PasswordResetToken resetToken) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(PasswordResetToken.ID_COLUMN, resetToken.getId());
        columns.put(PasswordResetToken.TOKEN_COLUMN, resetToken.getToken());
        columns.put(PasswordResetToken.PERSON_ID_COLUMN, resetToken.getPerson().getId());
        columns.put(PasswordResetToken.DATE_EXPIRED_COLUMN, resetToken.getExpiryDate());
        return columns;
    }

    @Override
    public RowMapper<PasswordResetToken> mapRow() {
        return new RowMapper<PasswordResetToken>() {
            @Override
            public PasswordResetToken mapRow(ResultSet resultSet, int i) throws SQLException {
                PasswordResetToken verificationToken = new PasswordResetToken();
                verificationToken.setId(resultSet.getLong(PasswordResetToken.ID_COLUMN));
                verificationToken.setToken(resultSet.getString(PasswordResetToken.TOKEN_COLUMN));
                verificationToken.setPerson(new Person(resultSet.getLong(PasswordResetToken.PERSON_ID_COLUMN)));
                verificationToken.setExpiryDate(resultSet.getDate(PasswordResetToken.DATE_EXPIRED_COLUMN));
                return verificationToken;
            }
        };
    }

    @Override
    public Optional<PasswordResetToken> findTokenByPersonId(Long id) {
        return queryForObject(FIND_TOKEN_PERSON_BY_ID, id);
    }

    @Override
    public Optional<PasswordResetToken> findOne(String token){
        return queryForObject(FIND_TOKEN, token);
    }
}


