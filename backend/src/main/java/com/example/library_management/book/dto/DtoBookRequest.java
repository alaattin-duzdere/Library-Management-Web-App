package com.example.library_management.book.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class DtoBookRequest {

    @NotEmpty
    private String title;

    @NotNull(message = "isbn is required")
    @Positive(message = "isbn must be a positive number")
    @Digits(integer = 13, fraction = 0, message = "isbn must be numeric and at most 13 digits")
    private Long isbn;

    @NotEmpty(message = "At least one author is required")
    private Set<Long> authors = new HashSet<>();

    @NotEmpty(message = "At least one category is required")
    private Set<Long> categories = new HashSet<>();

    @NotNull(message = "numberOfPages is required")
    @Min(value = 1, message = "numberOfPages must be at least 1")
    private Integer numberOfPages;
}
