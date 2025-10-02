package com.example.marketplace_backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageType {

    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, 1004, "kayıt bulunamadı"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "email bulunamadı"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, 1002, "email zaten kayıtlı"),
    PASSWORD_ALREADY_EXISTS(HttpStatus.CONFLICT, 1003, "bu şifre zaten kullanılıyor"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 1005, "token süresi dolmuş"),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, 1006, "kullanıcı adı bulunamadı"),
    USERNAME_OR_PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, 1007, "kullanıcı adı veya şifre hatalı"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, 1008, "refresh token bulunamadı"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 1009, "refresh token süresi dolmuş"),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, 1010, "kimlik doğrulama hatası"),
    USER_NOT_VERIFIED(HttpStatus.FORBIDDEN, 1011, "kullanıcı doğrulanmamış"),
    DATABASE_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2001, "veritabanı erişim hatası"),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3001, "email gönderme hatası"),
    GENERAL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "genel bir hata oluştu");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    MessageType(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
