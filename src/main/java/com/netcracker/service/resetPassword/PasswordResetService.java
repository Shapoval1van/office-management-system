package com.netcracker.service.resetPassword;


import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.model.entity.Person;

public interface PasswordResetService {
     public PasswordResetToken resetPassword(String email);
     public Person updatePasswordForPersonByEmail(String password, String token) throws OutdatedTokenException;
}
