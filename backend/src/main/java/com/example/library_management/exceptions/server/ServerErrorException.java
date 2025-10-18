package com.example.library_management.exceptions.server;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class ServerErrorException extends BaseApiException {

    /**
     * @param message Hatayı açıklayan ve loglanacak olan spesifik mesaj.
     */
    public ServerErrorException(String message) {
        super(ApiStatus.ERROR_SERVER_GENERAL, message);
    }

    /**
     * @param message Hatayı açıklayan ve loglanacak olan spesifik mesaj.
     * @param cause Orijinal hata (root cause), loglama ve hata ayıklama için.
     */
    public ServerErrorException(String message, Throwable cause) {
        super(ApiStatus.ERROR_SERVER_GENERAL, message, cause);
    }
}
