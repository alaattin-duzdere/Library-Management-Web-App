package com.example.library_management.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long accessTokenExpiresIn;

    private Long refreshTokenExpiresIn;
}
