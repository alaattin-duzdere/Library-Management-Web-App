package com.example.marketplace_backend.user.controller.impl;

import com.example.marketplace_backend.user.controller.IAuthController;
import com.example.marketplace_backend.user.dto.AuthRequest;
import com.example.marketplace_backend.user.dto.AuthResponse;
import com.example.marketplace_backend.user.dto.DtoUser;
import com.example.marketplace_backend.user.dto.LoginRequest;
import com.example.marketplace_backend.user.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements IAuthController {

    private final IAuthService authService;

    public AuthControllerImpl(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Override
    public DtoUser register(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.register(loginRequest);
    }

    @PostMapping("/login")
    @Override
    public AuthResponse login(@Valid @RequestBody AuthRequest input) {
        return authService.login(input);
    }
}
