package com.example.library_management.user.service.password;

public interface IPasswordResetStrategy {
    String sendResetToken(String email);
}
