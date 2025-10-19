package com.example.library_management.user.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.user.dto.*;
import org.springframework.http.ResponseEntity;

public interface IAuthController {

    ResponseEntity<CustomResponseBody<DtoUser>> register(RegisterRequest registerRequest);

    ResponseEntity<CustomResponseBody<LoginResponse>> login(LoginRequest input);

    ResponseEntity<CustomResponseBody<LoginResponse>>refreshToken(RefreshTokenRequest input);

    ResponseEntity<CustomResponseBody<String>> verifyUser(String token);

    public ResponseEntity<CustomResponseBody<String>> forgotPassword(ForgotPasswordRequest input);

    public ResponseEntity<Void> handleresetpassword(String email);

    public ResponseEntity<CustomResponseBody<String>> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
