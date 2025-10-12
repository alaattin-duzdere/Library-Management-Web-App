package com.example.library_management.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotEmpty
    private String token;

    @NotEmpty
    @Size(min = 8, max = 64, message = "Şifre en az 8 karakter olmalıdır")
    private String newPassword;

    @NotEmpty
    @Size(min = 8, max = 64, message = "Şifre en az 8 karakter olmalıdır")
    private String confirmNewPassword;
}
