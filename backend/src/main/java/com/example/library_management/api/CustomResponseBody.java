package com.example.library_management.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponseBody<T> {
    private boolean success;
    private final int httpStatus;
    private final String code; // The unique application code (e.g., S200, E404-RES)
    private final String message;
    private final T data;

    private CustomResponseBody(ApiStatus apiStatus, String message, T data) {
        this.success = apiStatus.getHttpStatus().is2xxSuccessful();
        this.httpStatus = apiStatus.getHttpStatus().value();
        this.code = apiStatus.getCode();
        // Use custom message if provided, otherwise use the default
        this.message = (message != null && !message.isEmpty()) ? message : apiStatus.getDefaultMessage();
        this.data = data;
    }

    public static <T> CustomResponseBody<T> success(ApiStatus status, T data, String message) {
        return new CustomResponseBody<>(status, message, data);
    }

    public static <T> CustomResponseBody<T> ok(T data, String message) {
        return success(ApiStatus.SUCCESS_OK, data, message);
    }

    public static <T> CustomResponseBody<T> failure(ApiStatus status, String message) {
        return new CustomResponseBody<>(status, message, null);
    }
}
