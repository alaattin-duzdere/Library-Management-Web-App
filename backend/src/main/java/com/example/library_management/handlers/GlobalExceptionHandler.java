package com.example.library_management.handlers;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.exceptions.BaseApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseApiException.class)
    public ResponseEntity<CustomResponseBody<?>> handleBaseApiException(BaseApiException ex) {

        ApiStatus apiStatus = ex.getApiStatus();

        // 1. Create the standardized failure body using the ApiStatus
        CustomResponseBody<?> body = CustomResponseBody.failure(apiStatus, ex.getMessage());

        // 2. Wrap the body in ResponseEntity, explicitly setting the HTTP Header Status
        //    using the standard HttpStatus derived from the ApiStatus enum.
        return new ResponseEntity<>(body, apiStatus.getHttpStatus());
    }

    /**
     * Handles validation errors by combining all error messages into a single,
     * human-readable string. It uses the standard 'failure' response format without a 'data' field.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponseBody<?>> handleValidationExceptionsSimple(
            MethodArgumentNotValidException ex) {

        // 1. Tüm validasyon hata mesajlarını alıp, virgülle ayırarak tek bir String'de birleştir.
        String combinedErrorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        // 2. 'failure' formatında bir CustomResponseBody oluştur.
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_INVALID_INPUT, // Veya validasyon için özel bir ApiStatus kodu
                combinedErrorMessage // Birleştirilmiş hata mesajını buraya koyuyoruz
        );

        // 3. ResponseEntity ile 400 Bad Request durum kodunu ve body'yi döndür.
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles expired JWTs and maps them to a standard API error.
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CustomResponseBody<?>> handleExpiredJwtException(ExpiredJwtException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_EXPIRED_TOKEN,
                "Your session has expired. Please log in again."
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles malformed or structurally incorrect JWTs.
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<CustomResponseBody<?>> handleMalformedJwtException(MalformedJwtException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_INVALID_TOKEN,
                "The provided token is malformed."
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles JWTs with an invalid signature, indicating potential tampering.
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<CustomResponseBody<?>> handleSignatureException(SignatureException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_INVALID_TOKEN,
                "The provided token has an invalid signature."
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Fallback handler for all unexpected exceptions (e.g., NullPointerException).
     * This ensures everything maps to a 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponseBody<?>> handleGenericException(Exception ex) {

        ApiStatus apiStatus = ApiStatus.ERROR_INTERNAL_SERVER;

        // Log the severe error (important for 500s)
        System.err.println("UNEXPECTED SERVER ERROR: " + ex.getMessage());

        CustomResponseBody<?> body = CustomResponseBody.failure(
                apiStatus,
                // Only show a generic message for security/simplicity
                apiStatus.getDefaultMessage()
        );

        return new ResponseEntity<>(body, apiStatus.getHttpStatus());
    }
}
