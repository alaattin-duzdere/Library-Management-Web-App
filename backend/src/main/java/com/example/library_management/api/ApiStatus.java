package com.example.library_management.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiStatus {

    // --- SUCCESS CODES (2xx) ---
    SUCCESS_OK(HttpStatus.OK, "S200", "Operation completed successfully."),
    SUCCESS_CREATED(HttpStatus.CREATED, "S201", "Resource created successfully."),
    SUCCESS_NO_CONTENT(HttpStatus.NO_CONTENT, "S204", "Operation completed, no content returned."),
    SUCCESS_ASYNC_ACCEPTED(HttpStatus.ACCEPTED, "S202", "Request accepted for async processing."),


    // --- CLIENT ERROR CODES (4xx) ---
    // General Validation/Bad Request
    ERROR_INVALID_INPUT(HttpStatus.BAD_REQUEST, "E400-GEN", "One or more input fields are invalid."),
    ERROR_PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE,"UNKNOWN","Payload too large"),
    ERROR_UNSUPPORTED_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"UNKNOWN","Unsported file type"),

    // Resource Errors
    ERROR_RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E404-RES", "The requested resource was not found."),
    ERROR_CONFLICT(HttpStatus.CONFLICT, "E409-CON", "Resource already exists or state prevents action."),

    // Authentication/Authorization Errors
    ERROR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E401-AUTH", "Authentication is required or has failed."),
    ERROR_FORBIDDEN(HttpStatus.FORBIDDEN, "E403-AUTH", "You do not have permission to perform this action."),
    ERROR_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404-USER", "The specified user does not exist."),

    // --- SERVER ERROR CODES (5xx) ---
    ERROR_INTERNAL_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "E500-SYS", "An unexpected server error occurred."),
    ERROR_EMAIL_SERVICE_FAILURE(HttpStatus.BAD_GATEWAY, "E502-MAIL", "Failed to send email."),

    // Authentication & Authorization Errors
    ERROR_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "E401-CRED", "Invalid email or password."),
    ERROR_USER_NOT_VERIFIED(HttpStatus.FORBIDDEN, "E403-VER", "User account is not verified."),

    // Token Errors
    ERROR_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "E401-TKN", "The provided token is invalid or malformed."),
    ERROR_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "E401-EXP", "The provided token has expired."),

    // Input/Validation Errors
    ERROR_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "E400-PWM", "Passwords do not match."),
    ERROR_DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500-DB", "A database error occurred."),
    ERROR_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E405-MTD", "The request method is not supported for this endpoint."),
    ERROR_BAD_REQUEST(HttpStatus.BAD_REQUEST, "E400-BAD", "The request body is malformed or unreadable."),
    ERROR_NOT_FOUND(HttpStatus.NOT_FOUND, "E404-NOT", "The requested resource was not found."),

    ERROR_CLIENT_GENERAL(HttpStatus.BAD_REQUEST, "E400-GEN", "A general client-side error occurred."),
    ERROR_SERVER_GENERAL(HttpStatus.INTERNAL_SERVER_ERROR, "E500-GEN", "A general server-side error occurred.");

    private final HttpStatus httpStatus;
    private final String code; // Your unique application status code
    private final String defaultMessage;

    ApiStatus(HttpStatus httpStatus, String code, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

}
