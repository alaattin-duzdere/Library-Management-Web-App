package com.example.library_management.exceptions.auth;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class UserNotVerifiedException extends BaseApiException {
    public UserNotVerifiedException(String email) {
        super(ApiStatus.ERROR_USER_NOT_VERIFIED,
                String.format("User with email '%s' has not verified their account.", email));
    }
}