package com.example.library_management.user.service.verification;

import com.example.library_management.user.model.User;

public interface IVerificationStrategy {
    void sendVerification(User user);
    String verify(String token);
}
