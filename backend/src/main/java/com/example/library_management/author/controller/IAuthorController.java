package com.example.library_management.author.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.author.dto.DtoAuthorRequest;
import com.example.library_management.author.dto.DtoAuthorResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAuthorController {

    ResponseEntity<CustomResponseBody<DtoAuthorResponse>> saveAuthor(DtoAuthorRequest input);

    ResponseEntity<CustomResponseBody<DtoAuthorResponse>> getAuthorById(Long id);

    ResponseEntity<CustomResponseBody<List<DtoAuthorResponse>>> getAllAuthors();

    ResponseEntity<CustomResponseBody<DtoAuthorResponse>> updateAuthor(Long id, DtoAuthorRequest input);

    ResponseEntity<CustomResponseBody<Boolean>> deleteAuthor(Long id);

}
