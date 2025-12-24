package com.example.library_management.exceptions.client;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class ConflictException extends BaseApiException {

    public ConflictException(String message) {
        super(ApiStatus.ERROR_CONFLICT, message);
    }

    public ConflictException(String resourceName, String field, Object value) {
        super(ApiStatus.ERROR_CONFLICT, String.format("%s with %s '%s' already exists.", resourceName, field, value));
    }
}
