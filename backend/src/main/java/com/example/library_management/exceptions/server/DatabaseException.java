package com.example.library_management.exceptions.server;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class DatabaseException extends BaseApiException {

    public DatabaseException(String message) {
        super(ApiStatus.ERROR_DATABASE_ERROR, message);
    }
}
