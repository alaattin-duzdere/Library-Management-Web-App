package com.example.marketplace_backend.user.controller.impl;

import com.example.marketplace_backend.common.model.RootEntity;
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

import static com.example.marketplace_backend.common.model.RootEntity.ok;

@RestController
public class AuthControllerImpl implements IAuthController {

    private final IAuthService authService;

    public AuthControllerImpl(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Override
    public RootEntity<DtoUser> register(@Valid @RequestBody LoginRequest loginRequest) {
        return ok(authService.register(loginRequest));
    }

    @PostMapping("/login")
    @Override
    public RootEntity<AuthResponse> login(@Valid @RequestBody AuthRequest input) {
        return ok(authService.login(input));
    }
}
