package com.example.library_management.exceptions.client;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class InvalidFileTypeException extends BaseApiException {
    public InvalidFileTypeException(String message) {
        super(ApiStatus.ERROR_UNSUPPORTED_FILE_TYPE,message);
    }
}
