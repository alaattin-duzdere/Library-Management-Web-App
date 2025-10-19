package com.example.library_management.user.controller.impl;


import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.exceptions.auth.ExpiredTokenException;
import com.example.library_management.exceptions.auth.InvalidTokenException;
import com.example.library_management.user.controller.IAuthController;
import com.example.library_management.user.dto.*;
import com.example.library_management.user.model.User;
import com.example.library_management.user.model.VerificationToken;
import com.example.library_management.user.repository.UserRepository;
import com.example.library_management.user.repository.VerificationTokenRepository;
import com.example.library_management.user.service.IAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class AuthControllerImpl implements IAuthController {

    private final IAuthService authService;

    private final VerificationTokenRepository tokenRepository;

    private final UserRepository userRepository;

    public AuthControllerImpl(IAuthService authService, VerificationTokenRepository tokenRepository, UserRepository userRepository) {
        this.authService = authService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("${api.auth.register}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoUser>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.warn("Inside register controller");
        log.warn("Registration attempt for user: {}", registerRequest.getUsername());
        CustomResponseBody<DtoUser> body = CustomResponseBody.ok(authService.register(registerRequest), "User registered successfully. Please check your email for verification instructions.");
        return new ResponseEntity<>(body,HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("${api.auth.login}")
    @Override
    public ResponseEntity<CustomResponseBody<LoginResponse>> login(@Valid @RequestBody LoginRequest input) {
        log.warn("Login attempt for user: {}", input.getEmail());
        CustomResponseBody<LoginResponse> body = CustomResponseBody.ok(authService.login(input), "Login successful");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("${api.auth.refresh}")
    @Override
    public ResponseEntity<CustomResponseBody<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
        CustomResponseBody<LoginResponse> body = CustomResponseBody.ok(authService.refreshToken(input),"Token refreshed successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("${api.auth.verify}")
    @Override
    public ResponseEntity<CustomResponseBody<String>> verifyUser(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token).orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        CustomResponseBody<String> body = CustomResponseBody.ok("User verified successfully", "User verified successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("${api.auth.forgot-password}")
    public ResponseEntity<CustomResponseBody<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        CustomResponseBody<String> body = CustomResponseBody.ok(authService.forgotPassword(forgotPasswordRequest.getEmail()), "Password reset instructions sent to email if it exists in our system");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("${api.auth.reset-password-handle}")
    @Override
    public ResponseEntity<Void> handleresetpassword(@RequestParam("token") String token) {
        return authService.handleResetPassword(token);
    }

    @Override
    @PostMapping("${api.auth.reset-password-submit}")
    public ResponseEntity<CustomResponseBody<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.warn("Input for /api/auth/reset-password-submit" + resetPasswordRequest);
        CustomResponseBody<String> body = CustomResponseBody.ok(authService.resetPassword(resetPasswordRequest), "Password has been reset successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
