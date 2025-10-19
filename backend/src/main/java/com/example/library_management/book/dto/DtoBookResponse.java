package com.example.library_management.book.dto;

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

    public int numberOfPages;
}
