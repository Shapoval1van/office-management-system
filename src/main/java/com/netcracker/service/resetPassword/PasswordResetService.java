package com.netcracker.service.resetPassword;


import com.netcracker.model.entity.PasswordResetToken;

public interface PasswordResetService {
     public PasswordResetToken resetPassword(String email);
}
