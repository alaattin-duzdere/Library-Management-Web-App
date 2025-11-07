package com.example.library_management.exceptions.server;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class EmailServiceException extends BaseApiException {

    /**
     * Constructs a new EmailServiceException.
     * @param message A descriptive message about the failure.
     * @param cause The original exception that was caught (e.g., MailException), for logging purposes.
     */
    public EmailServiceException(String message, Throwable cause) {
        super(ApiStatus.ERROR_EMAIL_SERVICE_FAILURE, message, cause);
    }

    public EmailServiceException(String message) {
        super(ApiStatus.ERROR_EMAIL_SERVICE_FAILURE,message);
    }
}