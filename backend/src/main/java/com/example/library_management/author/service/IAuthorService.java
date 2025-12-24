package com.example.library_management.author.service;

import com.example.library_management.author.dto.DtoAuthorRequest;
import com.example.library_management.author.dto.DtoAuthorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAuthorService {
    DtoAuthorResponse saveAuthor(DtoAuthorRequest input);

    DtoAuthorResponse getAuthorById(Long id);

    Page<DtoAuthorResponse> getAllAuthors(String query, Pageable pageable);

    DtoAuthorResponse updateAuthor(Long id, DtoAuthorRequest input);

    boolean deleteAuthor(Long id);
}
