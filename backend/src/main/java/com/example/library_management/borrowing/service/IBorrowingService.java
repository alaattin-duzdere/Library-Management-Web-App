package com.example.library_management.borrowing.service;

import com.example.library_management.borrowing.dto.DtoBorrowResponse;

import java.util.List;

public interface IBorrowingService {
    DtoBorrowResponse borrowBook(Long bookId);

    DtoBorrowResponse getBorrowingDetails(Long borrowingId);

    List<DtoBorrowResponse> getBorrowingByUserId(Long userId);

    DtoBorrowResponse returnBook(Long borrowingId);

}
