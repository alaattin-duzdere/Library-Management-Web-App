package com.example.library_management.exceptions.client;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class PasswordMismatchException extends BaseApiException {
    public PasswordMismatchException() {
        super(ApiStatus.ERROR_PASSWORD_MISMATCH);
    }
}
