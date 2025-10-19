package com.example.library_management.exceptions.auth;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class ForbiddenException extends BaseApiException {

    public ForbiddenException(String message) {
        super(ApiStatus.ERROR_FORBIDDEN, message);
    }

    public ForbiddenException() {
        super(ApiStatus.ERROR_FORBIDDEN);
    }
}
