package com.example.library_management.book.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookController {

    ResponseEntity<CustomResponseBody<DtoBookResponse>> saveBook(DtoBookRequest dtoBookRequest);

    ResponseEntity<CustomResponseBody<DtoBookResponse>> uploadPhoto(Long bookId,MultipartFile file);

    ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookById(Long bookId);

    ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookByIsbn(Long isbn);

    ResponseEntity<CustomResponseBody<Page<DtoBookResponse>>> getAllBooks(Pageable pageable,String search, Long categoryId, Long authorId);

    ResponseEntity<CustomResponseBody<DtoBookResponse>> updateBook(Long bookId, DtoBookRequest dtoBookRequest);

    ResponseEntity<CustomResponseBody<Boolean>> deleteBook(Long bookId);

}
