package com.example.library_management.book.service;

import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IBookService {

    DtoBookResponse saveBook(DtoBookRequest dtoBookRequest);

    DtoBookResponse uploadPhoto(Long bookId, MultipartFile file);

    DtoBookResponse getBookById(Long bookId);

    DtoBookResponse getBookByIsbn(Long isbn);

    Page<DtoBookResponse> getAllBooks(Pageable pageable);

    DtoBookResponse updateBook(Long bookId, DtoBookRequest dtoBookRequest);

    Boolean deleteBook(Long bookId);
}
