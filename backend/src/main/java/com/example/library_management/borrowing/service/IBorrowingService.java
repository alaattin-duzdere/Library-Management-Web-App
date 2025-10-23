package com.example.library_management.borrowing.service;

import com.example.library_management.borrowing.dto.DtoBorrowResponse;

public interface IBorrowingService {
    DtoBorrowResponse borrowBook(Long bookId);

    DtoBorrowResponse getBorrowingDetails(Long borrowingId);

    DtoBorrowResponse getBorrowingByUserId(Long userId);

    DtoBorrowResponse getBorrowingByBookId(Long bookId);

    DtoBorrowResponse returnBook(Long borrowingId);

}
