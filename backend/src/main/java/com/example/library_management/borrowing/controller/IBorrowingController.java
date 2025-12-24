package com.example.library_management.borrowing.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.borrowing.dto.DtoBorrowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IBorrowingController {

    ResponseEntity<CustomResponseBody<DtoBorrowResponse>> borrowBook(Long bookId);

    ResponseEntity<CustomResponseBody<Page<DtoBorrowResponse>>> getBorrowings(Pageable pageable,Long borrowingId,Long userId,Long bookId);

    ResponseEntity<CustomResponseBody<Page<DtoBorrowResponse>>> getMyBorrowings(Pageable pageable);

    ResponseEntity<CustomResponseBody<DtoBorrowResponse>> returnBook(Long borrowingId);
}
