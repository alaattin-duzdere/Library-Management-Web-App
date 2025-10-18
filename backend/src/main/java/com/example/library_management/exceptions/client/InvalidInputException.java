package com.example.library_management.exceptions.client;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class InvalidInputException extends BaseApiException {

    public InvalidInputException(String message) {
        super(ApiStatus.ERROR_INVALID_INPUT, message);
    }

    // Use default message
    public InvalidInputException() {
        super(ApiStatus.ERROR_INVALID_INPUT);
    }
}
