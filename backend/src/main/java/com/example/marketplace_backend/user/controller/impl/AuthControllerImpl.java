package com.example.marketplace_backend.user.controller.impl;

import com.example.marketplace_backend.common.model.RootEntity;
import com.example.marketplace_backend.exception.BaseException;
import com.example.marketplace_backend.exception.ErrorMessage;
import com.example.marketplace_backend.exception.MessageType;
import com.example.marketplace_backend.user.controller.IAuthController;
import com.example.marketplace_backend.user.dto.AuthRequest;
import com.example.marketplace_backend.user.dto.AuthResponse;
import com.example.marketplace_backend.user.dto.DtoUser;
import com.example.marketplace_backend.user.dto.LoginRequest;
import com.example.marketplace_backend.user.model.User;
import com.example.marketplace_backend.user.model.VerificationToken;
import com.example.marketplace_backend.user.repository.UserRepository;
import com.example.marketplace_backend.user.repository.VerificationTokenRepository;
import com.example.marketplace_backend.user.service.IAuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.marketplace_backend.common.model.RootEntity.ok;

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
}
