package com.example.library_management.exceptions.auth;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class UnauthorizedException extends BaseApiException {

    public UnauthorizedException(String message) {
        super(ApiStatus.ERROR_UNAUTHORIZED, message);
    }

    public UnauthorizedException() {
        super(ApiStatus.ERROR_UNAUTHORIZED);
    }
}
