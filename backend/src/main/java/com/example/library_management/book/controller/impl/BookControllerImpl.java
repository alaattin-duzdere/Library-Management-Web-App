package com.example.library_management.book.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.book.controller.IBookController;
import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.service.IBookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookControllerImpl implements IBookController {

    private final IBookService bookService;

    public BookControllerImpl(IBookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/api/books")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> saveBook(@RequestBody @Valid DtoBookRequest dtoBookRequest) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.saveBook(dtoBookRequest), "Book created successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookById(@PathVariable Long bookId) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.getBookById(bookId), " Book retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/books/isbn/{isbn}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookByIsbn(@PathVariable Long isbn) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.getBookByIsbn(isbn), " Book retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/books")
    @Override
    public ResponseEntity<CustomResponseBody<List<DtoBookResponse>>> getAllBooks() {
        CustomResponseBody<List<DtoBookResponse>> body = CustomResponseBody.ok(bookService.getAllBooks(), " All books retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PutMapping("/api/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> updateBook(@PathVariable Long bookId, @RequestBody @Valid DtoBookRequest dtoBookRequest) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.updateBook(bookId,dtoBookRequest), " Book updated successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @DeleteMapping("/api/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<Boolean>> deleteBook(@PathVariable Long bookId) {
        CustomResponseBody<Boolean> body = CustomResponseBody.ok(bookService.deleteBook(bookId), " Book deleted successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
