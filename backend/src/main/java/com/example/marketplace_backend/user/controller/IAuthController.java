package com.example.marketplace_backend.user.controller;

import com.example.marketplace_backend.common.model.RootEntity;
import com.example.marketplace_backend.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAuthController {

    RootEntity<DtoUser> register(LoginRequest loginRequest);

    RootEntity<AuthResponse> login(AuthRequest input);

    RootEntity<AuthResponse> refreshToken(RefreshTokenRequest input);

    RootEntity<String> verifyUser(String token);

    public RootEntity <String> forgotPassword(ForgotPasswordRequest input);

    public ResponseEntity<Void> handleresetpassword(String email);

    public RootEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
