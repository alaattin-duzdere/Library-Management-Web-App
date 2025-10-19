package com.example.library_management.author.service;

import com.example.library_management.author.dto.DtoAuthorRequest;
import com.example.library_management.author.dto.DtoAuthorResponse;

import java.util.List;

public interface IAuthorService {
    DtoAuthorResponse saveAuthor(DtoAuthorRequest input);

    DtoAuthorResponse getAuthorById(Long id);

    List<DtoAuthorResponse> getAllAuthors();

    DtoAuthorResponse updateAuthor(Long id, DtoAuthorRequest input);

    boolean deleteAuthor(Long id);
}
