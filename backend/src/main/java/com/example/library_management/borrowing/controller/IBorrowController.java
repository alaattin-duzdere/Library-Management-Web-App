package com.example.library_management.borrowing.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.borrowing.dto.DtoBorrowResponse;
import org.springframework.http.ResponseEntity;

public interface IBorrowController {

    ResponseEntity<CustomResponseBody<DtoBorrowResponse>> borrowBook(Long bookId);

    ResponseEntity<CustomResponseBody<DtoBorrowResponse>> getBorrowingDetails(Long borrowingId);

    ResponseEntity<CustomResponseBody<DtoBorrowResponse>> getBorrowingByUserId(Long userId);

    ResponseEntity<CustomResponseBody<DtoBorrowResponse>> getBorrowingByBookId(Long bookId);

    ResponseEntity<CustomResponseBody<DtoBorrowResponse>> returnBook(Long borrowingId);
}
