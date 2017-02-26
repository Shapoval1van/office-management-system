package com.netcracker.service.resetPassword;


import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;

public interface PasswordResetService {
     public Token resetPassword(String email, String link);
     public Person updatePasswordForPersonByEmail(String password, String token) throws OutdatedTokenException;
}
