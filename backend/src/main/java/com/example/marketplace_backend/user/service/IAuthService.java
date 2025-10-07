package com.example.marketplace_backend.user.service;

import com.example.marketplace_backend.user.dto.*;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    DtoUser register(LoginRequest loginRequest);

    AuthResponse login(AuthRequest input);

    AuthResponse refreshToken(RefreshTokenRequest input);

    String forgotPassword(String email);

    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    ResponseEntity<Void> handleResetPassword(String token);
}
