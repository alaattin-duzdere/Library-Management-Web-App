package com.example.library_management.exceptions.auth;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class InvalidTokenException extends BaseApiException {
    public InvalidTokenException() {
        super(ApiStatus.ERROR_INVALID_TOKEN);
    }

    public InvalidTokenException(String message) {
        super(ApiStatus.ERROR_INVALID_TOKEN, message);
    }
}
