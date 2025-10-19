package com.example.library_management.exceptions;

import com.example.library_management.api.ApiStatus;
import lombok.Getter;

@Getter
public abstract class BaseApiException extends RuntimeException {

    private final ApiStatus apiStatus;

    public BaseApiException(ApiStatus apiStatus, String message) {
        super(message);
        this.apiStatus = apiStatus;
    }

    // Optional: for cases where no message is provided
    public BaseApiException(ApiStatus apiStatus) {
        super(apiStatus.getDefaultMessage());
        this.apiStatus = apiStatus;
    }

    // New constructor accepting a Throwable cause
    public BaseApiException(ApiStatus apiStatus, Throwable cause) {
        super(apiStatus.getDefaultMessage(), cause);
        this.apiStatus = apiStatus;
    }

    public BaseApiException(ApiStatus apiStatus, String message, Throwable cause) {
        super(message != null ? message : apiStatus.getDefaultMessage(), cause);
        this.apiStatus = apiStatus;
    }

}