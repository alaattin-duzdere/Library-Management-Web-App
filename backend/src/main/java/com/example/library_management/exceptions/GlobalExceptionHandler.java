package com.example.library_management.exceptions;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.api.CustomResponseBody;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. CORE & BUSINESS EXCEPTIONS

    @ExceptionHandler(BaseApiException.class)
    public ResponseEntity<CustomResponseBody<?>> handleBaseApiException(BaseApiException ex) {
        ApiStatus apiStatus = ex.getApiStatus();
        CustomResponseBody<?> body = CustomResponseBody.failure(apiStatus, ex.getMessage());
        return new ResponseEntity<>(body, apiStatus.getHttpStatus());
    }

    // 2. SECURITY & AUTHENTICATION EXCEPTIONS

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CustomResponseBody<?>> handleExpiredJwtException(ExpiredJwtException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_EXPIRED_TOKEN,
                "Your session has expired. Please log in again."
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<CustomResponseBody<?>> handleMalformedJwtException(MalformedJwtException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_INVALID_TOKEN,
                "The provided token is malformed."
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<CustomResponseBody<?>> handleSignatureException(SignatureException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_INVALID_TOKEN,
                "The provided token has an invalid signature."
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomResponseBody<?>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_USER_NOT_FOUND,
                "The specified user does not exist."
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomResponseBody<?>> handleAccessDeniedForMethodSecurity() {
        CustomResponseBody<Object> body = CustomResponseBody.failure(
                ApiStatus.ERROR_FORBIDDEN,
                "Access denied"
        );
        return new ResponseEntity<>(body, HttpStatus.valueOf(body.getHttpStatus()));
    }

    // 3. VALIDATION & INPUT HANDLING

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponseBody<?>> handleValidationExceptionsSimple(MethodArgumentNotValidException ex) {
        String combinedErrorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_INVALID_INPUT,
                combinedErrorMessage
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponseBody<?>> handleHttpMessageNotReadableException() {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_BAD_REQUEST,
                "The request body is malformed or unreadable."
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomResponseBody<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        CustomResponseBody<?> body = CustomResponseBody.failure(ApiStatus.ERROR_BAD_REQUEST, message);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomResponseBody<?>> handleConstraintViolation(ConstraintViolationException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(ApiStatus.ERROR_INVALID_INPUT, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<CustomResponseBody<?>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_UNSUPPORTED_FILE_TYPE,
                "Content type '" + ex.getContentType() + "' not supported"
        );
        return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // 4. DATABASE & DATA INTEGRITY

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponseBody<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // Not: Burada unique constraint hataları (örn: aynı email ile kayıt) genelde yakalanır.
        CustomResponseBody<Object> body = CustomResponseBody.failure(
                ApiStatus.ERROR_DATA_INTEGRITY_VIOLATION,
                "Data integrity violation. This implies a conflict (e.g., duplicate entry)."
        );
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    // 5. WEB & HTTP INFRASTRUCTURE

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomResponseBody<?>> handleRequestMethodNotSupportedException() {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_METHOD_NOT_ALLOWED,
                "The request method is not supported for this endpoint."
        );
        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<CustomResponseBody<?>> handleNotFoundExceptions() {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_NOT_FOUND,
                "The requested resource was not found."
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CustomResponseBody<?>> handleMaxUploadSizeEx() {
        CustomResponseBody<?> body = CustomResponseBody.failure(
                ApiStatus.ERROR_PAYLOAD_TOO_LARGE,
                "The requested body was over the max size limit."
        );
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    // 6. GLOBAL FALLBACK

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponseBody<?>> handleGenericException(Exception ex) {
        ApiStatus apiStatus = ApiStatus.ERROR_INTERNAL_SERVER;
        log.error("Unhandled Global Exception occurred:", ex);

        CustomResponseBody<?> body = CustomResponseBody.failure(
                apiStatus,
                apiStatus.getDefaultMessage()
        );

        return new ResponseEntity<>(body, apiStatus.getHttpStatus());
    }
}