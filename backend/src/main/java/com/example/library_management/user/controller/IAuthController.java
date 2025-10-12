package com.example.library_management.user.controller;

import com.example.library_management.common.model.RootEntity;
import com.example.library_management.user.dto.*;
import org.springframework.http.ResponseEntity;

public interface IAuthController {

    RootEntity<DtoUser> register(LoginRequest loginRequest);

    RootEntity<AuthResponse> login(AuthRequest input);

    RootEntity<AuthResponse> refreshToken(RefreshTokenRequest input);

    RootEntity<String> verifyUser(String token);

    public RootEntity <String> forgotPassword(ForgotPasswordRequest input);

    public ResponseEntity<Void> handleresetpassword(String email);

    public RootEntity<String> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
