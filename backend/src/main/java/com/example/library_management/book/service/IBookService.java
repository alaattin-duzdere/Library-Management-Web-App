package com.example.library_management.book.service;

import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;

import java.util.List;

public interface IBookService {

    DtoBookResponse saveBook(DtoBookRequest dtoBookRequest);

    DtoBookResponse getBookById(Long bookId);

    DtoBookResponse getBookByIsbn(Long isbn);

    List<DtoBookResponse> getAllBooks();

    DtoBookResponse updateBook(Long bookId, DtoBookRequest dtoBookRequest);

    Boolean deleteBook(Long bookId);
}
