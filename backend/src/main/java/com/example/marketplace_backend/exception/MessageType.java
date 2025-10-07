package com.example.marketplace_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {

    // --- 400 Bad Request (Client Input Errors) ---
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, 1000, "Passwords do not match"),

    // --- 401 Unauthorized (Authentication/Token Errors) ---
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 1005, "Token expired"),
    USERNAME_OR_PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, 1007, "Invalid username or password"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 1009, "Refresh token expired"),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, 1010, "Authentication error"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, 1012, "Token invalid"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, 1013, "Refresh token invalid"),

    // --- 403 Forbidden (Authorization/Verification Errors) ---
    // NOTE: It is best practice for error codes to be unique.
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, 1011, "Email not verified. Please check your inbox for verification."),
    USER_NOT_VERIFIED(HttpStatus.FORBIDDEN, 1014, "User not verified"), // Changed code from 1011 to 1014 for uniqueness

    // --- 404 Not Found (Resource Not Found Errors) ---
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "Email not found"),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, 1004, "Record not found"),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, 1006, "Username not found"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, 1008, "Refresh token not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2004, "User not found"),

    // --- 409 Conflict (Resource Conflict Errors) ---
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, 1002, "Email already exists"),
    PASSWORD_ALREADY_EXISTS(HttpStatus.CONFLICT, 1003, "This password is already in use"),

    // --- 500 Internal Server Error (Server-Side Errors) ---
    DATABASE_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2001, "Database access error"),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3001, "Email sending error"),
    GENERAL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "A general error occurred");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    MessageType(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
