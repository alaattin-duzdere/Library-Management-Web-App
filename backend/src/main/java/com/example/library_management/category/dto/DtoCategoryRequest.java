package com.example.library_management.category.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCategoryRequest {

    @NotEmpty(message = "Category name must not be empty")
    private String categoryName;
}
