package com.example.library_management.exceptions.client;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.exceptions.BaseApiException;

public class ResourceNotFoundException extends BaseApiException {
    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(
                ApiStatus.ERROR_RESOURCE_NOT_FOUND,
                String.format("%s not found with %s : '%s'", resourceName, field, value)
        );
    }
}
