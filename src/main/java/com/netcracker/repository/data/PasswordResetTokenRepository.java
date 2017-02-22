package com.netcracker.repository.data;

import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JdbcRepository<PasswordResetToken, Long> {
    public Optional<PasswordResetToken> findTokenByPersonId(Long id);
    public Optional<PasswordResetToken> findOne(String token);
}
