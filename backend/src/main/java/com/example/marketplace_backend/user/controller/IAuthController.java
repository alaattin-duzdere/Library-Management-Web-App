package com.example.marketplace_backend.user.controller;

import com.example.marketplace_backend.user.dto.AuthRequest;
import com.example.marketplace_backend.user.dto.AuthResponse;
import com.example.marketplace_backend.user.dto.DtoUser;
import com.example.marketplace_backend.user.dto.LoginRequest;

public interface IAuthController {

    DtoUser register(LoginRequest loginRequest);

    AuthResponse login(AuthRequest input);
}
