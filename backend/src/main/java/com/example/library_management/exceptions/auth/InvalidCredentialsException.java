package com.example.library_management.exceptions.auth;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class InvalidCredentialsException extends BaseApiException {
    public InvalidCredentialsException() {
        super(ApiStatus.ERROR_INVALID_CREDENTIALS);
    }
}
