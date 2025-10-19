package com.example.library_management.book.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBookController {

    ResponseEntity<CustomResponseBody<DtoBookResponse>> saveBook(DtoBookRequest dtoBookRequest);

    ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookById(Long bookId);

    ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookByIsbn(Long isbn);

    ResponseEntity<CustomResponseBody<List<DtoBookResponse>>> getAllBooks();

    ResponseEntity<CustomResponseBody<DtoBookResponse>> updateBook(Long bookId, DtoBookRequest dtoBookRequest);

    ResponseEntity<CustomResponseBody<Boolean>> deleteBook(Long bookId);

}
