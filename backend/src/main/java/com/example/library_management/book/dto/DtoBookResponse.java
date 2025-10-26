package com.example.library_management.book.dto;

import com.example.library_management.book.model.Situation;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DtoBookResponse {

    private Long id;

    private String title;

    private Long isbn;

    private Set<Long> authors = new HashSet<>();

    private Set<Long> categories = new HashSet<>();

    private int numberOfPages;

    private Situation situation;

    @Nullable()
    private String imageUrl;
}
