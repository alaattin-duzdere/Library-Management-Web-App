package com.example.marketplace_backend.user.service;

import com.example.marketplace_backend.user.dto.AuthRequest;
import com.example.marketplace_backend.user.dto.AuthResponse;
import com.example.marketplace_backend.user.dto.DtoUser;
import com.example.marketplace_backend.user.dto.LoginRequest;
import com.example.marketplace_backend.user.model.User;

public interface IAuthService {
    DtoUser register(LoginRequest loginRequest);

    AuthResponse login(AuthRequest input);
}
