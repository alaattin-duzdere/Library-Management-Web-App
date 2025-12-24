package com.example.library_management.user.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.user.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

public interface IAuthController {

    ResponseEntity<CustomResponseBody<DtoUser>> register(RegisterRequest registerRequest);

    ResponseEntity<CustomResponseBody<String>> resendVerification(ForgotPasswordRequest emailRequest);

    ResponseEntity<CustomResponseBody<LoginResponse>> login(LoginRequest input);

    ResponseEntity<CustomResponseBody<?>> logout(HttpServletRequest request);

    ResponseEntity<CustomResponseBody<LoginResponse>>refreshToken(RefreshTokenRequest input);

    RedirectView verifyUser(String token);

    public ResponseEntity<CustomResponseBody<String>> forgotPassword(ForgotPasswordRequest input);

    public ResponseEntity<Void> handleresetpassword(String email);

    public ResponseEntity<CustomResponseBody<String>> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
