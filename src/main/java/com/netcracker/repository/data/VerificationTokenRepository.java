package com.netcracker.repository.data;


import com.netcracker.model.entity.VerificationToken;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JdbcRepository<VerificationToken, Long> {
    Optional<VerificationToken> findVerificationTokenByPerson(Long personId);
    Optional<VerificationToken> findVerificationTokenByValueAndExpiredDate(String token);
}
