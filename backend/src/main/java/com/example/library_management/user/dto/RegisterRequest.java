package com.example.library_management.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotEmpty
    private String username;

    @NotEmpty
    @Size(min = 8, max = 64, message = "Şifre en az 8 karakter olmalıdır")
    private String password;

    @NotEmpty
    @Email
    private String email;
}
