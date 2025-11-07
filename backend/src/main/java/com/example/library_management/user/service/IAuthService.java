package com.example.library_management.user.service;

import com.example.library_management.user.dto.*;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    DtoUser register(RegisterRequest registerRequest);

    String verifyUser(String token);

    LoginResponse login(LoginRequest input);

    String logout(String token);

    LoginResponse refreshToken(RefreshTokenRequest input);

    String forgotPassword(String email);

    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    ResponseEntity<Void> handleResetPassword(String token);
}
