package com.example.library_management.borrowing.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.borrowing.controller.IBorrowController;
import com.example.library_management.borrowing.dto.DtoBorrowResponse;
import com.example.library_management.borrowing.service.IBorrowingService;
import org.hibernate.type.descriptor.java.JdbcTimeJavaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BorrowControllerImpl implements IBorrowController {

    private final IBorrowingService borrowingService;

    public BorrowControllerImpl(IBorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/api/borrow/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBorrowResponse>> borrowBook(@PathVariable Long bookId) {
        CustomResponseBody<DtoBorrowResponse> body = CustomResponseBody.ok(borrowingService.borrowBook(bookId), "Book borrowed successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/borrow/{borrowingId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBorrowResponse>> getBorrowingDetails(@PathVariable Long borrowingId) {
        CustomResponseBody<DtoBorrowResponse> body = CustomResponseBody.ok(borrowingService.getBorrowingDetails(borrowingId), "Borrowing details retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/borrow/user/{userId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBorrowResponse>> getBorrowingByUserId(@PathVariable Long userId) {
        CustomResponseBody<DtoBorrowResponse> body = CustomResponseBody.ok(borrowingService.getBorrowingByUserId(userId), "User borrowing details retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/borrow/book/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBorrowResponse>> getBorrowingByBookId(@PathVariable Long bookId) {
        CustomResponseBody<DtoBorrowResponse> body = CustomResponseBody.ok(borrowingService.getBorrowingByBookId(bookId), "Book borrowing details retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("/api/borrow/return/{borrowingId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBorrowResponse>> returnBook(@PathVariable Long borrowingId) {
        CustomResponseBody<DtoBorrowResponse> body = CustomResponseBody.ok(borrowingService.returnBook(borrowingId), "Book returned successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
