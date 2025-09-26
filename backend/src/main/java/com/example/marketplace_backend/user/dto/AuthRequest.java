package com.example.marketplace_backend.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
