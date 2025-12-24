package com.example.library_management.borrowing.service;

import com.example.library_management.borrowing.dto.DtoBorrowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBorrowingService {
    DtoBorrowResponse borrowBook(Long bookId);

    Page<DtoBorrowResponse> getBorrowings(Pageable pageable, Long borrowingId, Long userId, Long bookId);

    DtoBorrowResponse returnBook(Long borrowingId);

}
