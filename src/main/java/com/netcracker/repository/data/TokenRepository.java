package com.netcracker.repository.data;


import com.netcracker.model.entity.Token;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface TokenRepository extends JdbcRepository<Token, Long> {
    Optional<Token> findTokenByPerson(Long personId);
    Optional<Token> findTokenByValueAndExpiredDate(String token);
    Optional<Token> findTokenByValue(String token);
}