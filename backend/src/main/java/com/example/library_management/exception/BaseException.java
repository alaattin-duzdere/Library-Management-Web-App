package com.example.library_management.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final MessageType status;

    public BaseException(ErrorMessage errorMessage) {

        super(errorMessage.prepareErrorMessage());
        this.status = errorMessage.getMessageType();
    }
}
