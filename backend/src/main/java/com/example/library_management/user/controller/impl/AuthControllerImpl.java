package com.example.library_management.user.controller.impl;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.security.JwtService;
import com.example.library_management.security.TokenBlacklistService;
import com.example.library_management.user.controller.IAuthController;
import com.example.library_management.user.dto.*;
import com.example.library_management.user.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class AuthControllerImpl implements IAuthController {

    private final IAuthService authService;

    private final TokenBlacklistService blacklistService;

    private final JwtService jwtUtils;

    public AuthControllerImpl(IAuthService authService, TokenBlacklistService blacklistService, JwtService jwtUtils) {
        this.authService = authService;
        this.blacklistService = blacklistService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("${api.auth.register}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoUser>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.warn("Inside register controller");
        log.warn("Registration attempt for user: {}", registerRequest.getUsername());
        CustomResponseBody<DtoUser> body = CustomResponseBody.ok(authService.register(registerRequest), "User registered successfully. Please check your email for verification instructions.");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("${api.auth.verify}")
    @Override
    public ResponseEntity<CustomResponseBody<String>> verifyUser(@RequestParam("token") String token) {
        // TODO: redirect user if verify is successful to 'localhost:3000/login?verifySuccessful=true'
        String message = authService.verifyUser(token);
        CustomResponseBody<String> body = CustomResponseBody.ok(message, message);
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("${api.auth.login}")
    @Override
    public ResponseEntity<CustomResponseBody<LoginResponse>> login(@Valid @RequestBody LoginRequest input) {
        log.warn("Login attempt for user: {}", input.getEmail());
        CustomResponseBody<LoginResponse> body = CustomResponseBody.ok(authService.login(input), "Login successful");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("/api/auth/logout")
    @Override
    public ResponseEntity<CustomResponseBody<?>> logout(HttpServletRequest request) {

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);

            CustomResponseBody<String> body = CustomResponseBody.ok(authService.logout(token), "Successfully completed");
            return new ResponseEntity<>(body,HttpStatusCode.valueOf(body.getHttpStatus()));
        }

        CustomResponseBody<Object> body = CustomResponseBody.failure(ApiStatus.ERROR_INVALID_INPUT, "No token found to invalidate");
        return new ResponseEntity<>(body,HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("${api.auth.refresh}")
    @Override
    public ResponseEntity<CustomResponseBody<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
        CustomResponseBody<LoginResponse> body = CustomResponseBody.ok(authService.refreshToken(input),"Token refreshed successfully");
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
