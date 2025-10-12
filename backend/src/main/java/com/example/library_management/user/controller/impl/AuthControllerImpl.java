package com.example.library_management.user.controller.impl;

import com.example.library_management.common.model.RootEntity;
import com.example.library_management.exception.BaseException;
import com.example.library_management.exception.ErrorMessage;
import com.example.library_management.exception.MessageType;
import com.example.library_management.user.controller.IAuthController;
import com.example.library_management.user.dto.*;
import com.example.library_management.user.model.User;
import com.example.library_management.user.model.VerificationToken;
import com.example.library_management.user.repository.UserRepository;
import com.example.library_management.user.repository.VerificationTokenRepository;
import com.example.library_management.user.service.IAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.library_management.common.model.RootEntity.ok;

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
    public RootEntity<DtoUser> register(@Valid @RequestBody LoginRequest loginRequest) {
        log.warn("Inside register controller");
        log.warn("Registration attempt for user: {}", loginRequest.getUsername());
        return ok(authService.register(loginRequest));
    }

    @PostMapping("${api.auth.login}")
    @Override
    public RootEntity<AuthResponse> login(@Valid @RequestBody AuthRequest input) {
        log.warn("Login attempt for user: {}", input.getEmail());
        return ok(authService.login(input));
    }

    @PostMapping("${api.auth.refresh}")
    @Override
    public RootEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
        return ok(authService.refreshToken(input));
    }

    @GetMapping("${api.auth.verify}")
    @Override
    public RootEntity<String> verifyUser(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BaseException(new ErrorMessage(MessageType.TOKEN_EXPIRED,"Verification token has expired"));
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        return ok("User verified successfully");
    }

    @PostMapping("${api.auth.forgot-password}")
    public RootEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return ok(authService.forgotPassword(forgotPasswordRequest.getEmail()));
    }

    @GetMapping("${api.auth.reset-password-handle}")
    @Override
    public ResponseEntity<Void> handleresetpassword(@RequestParam("token") String token) {
        return authService.handleResetPassword(token);
    }

    @Override
    @PostMapping("${api.auth.reset-password-submit}")
    public RootEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.warn("Input for /api/auth/reset-password-submit" + resetPasswordRequest);
        return ok(authService.resetPassword(resetPasswordRequest));
    }
}
