package com.example.library_management.user.service;

import com.example.library_management.user.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

public interface IAuthService {
    DtoUser register(RegisterRequest registerRequest);

    String resendVerification(String email);

    RedirectView verifyUser(String token);

    LoginResponse login(LoginRequest input);

    String logout(String token);

    LoginResponse refreshToken(RefreshTokenRequest input);

    String forgotPassword(String email);

    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    ResponseEntity<Void> handleResetPassword(String token);
}
