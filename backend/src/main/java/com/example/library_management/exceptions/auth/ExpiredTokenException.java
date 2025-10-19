package com.example.library_management.exceptions.auth;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class ExpiredTokenException extends BaseApiException {
    public ExpiredTokenException() {
        super(ApiStatus.ERROR_EXPIRED_TOKEN);
    }

    public ExpiredTokenException(String message) {
        super(ApiStatus.ERROR_EXPIRED_TOKEN, message);
    }
}
