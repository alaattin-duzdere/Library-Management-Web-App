    package com.example.library_management.borrowing.controller.impl;

    import com.example.library_management.api.CustomResponseBody;
    import com.example.library_management.borrowing.controller.IBorrowingController;
    import com.example.library_management.borrowing.dto.DtoBorrowResponse;
    import com.example.library_management.borrowing.service.IBorrowingService;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.data.web.PageableDefault;
    import org.springframework.http.HttpStatusCode;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    @RestController
    public class BorrowingControllerImpl implements IBorrowingController {

        private final IBorrowingService borrowingService;

        public BorrowingControllerImpl(IBorrowingService borrowingService) {
            this.borrowingService = borrowingService;
        }

        @PostMapping("/api/borrowings/books/{bookId}")
        @Override
        public ResponseEntity<CustomResponseBody<DtoBorrowResponse>> borrowBook(@PathVariable Long bookId) {
            CustomResponseBody<DtoBorrowResponse> body = CustomResponseBody.ok(borrowingService.borrowBook(bookId), "Book borrowed successfully");
            return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
        }

        @GetMapping("/api/admin/borrowings")
        @Override
        public ResponseEntity<CustomResponseBody<Page<DtoBorrowResponse>>> getBorrowings(
                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                @RequestParam(required = false) Long borrowingId,
                @RequestParam(required = false) Long userId,
                @RequestParam(required = false) Long bookId) {
            Page<DtoBorrowResponse> borrowings = borrowingService.getBorrowings(pageable, borrowingId, userId, bookId);
            CustomResponseBody<Page<DtoBorrowResponse>> body = CustomResponseBody.ok(borrowings, "Borrowings retrieved successfully");
            return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
        }


        @GetMapping("api/borrowings/me")
        @Override
        public ResponseEntity<CustomResponseBody<Page<DtoBorrowResponse>>> getMyBorrowings(
                @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
            Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
            CustomResponseBody<Page<DtoBorrowResponse>> body = CustomResponseBody.ok(borrowingService.getBorrowings(pageable,null,userId,null), "User borrowing details retrieved successfully");
            return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
        }


        @PostMapping("api/borrowings/{borrowingId}/return")
        @Override
        public ResponseEntity<CustomResponseBody<DtoBorrowResponse>> returnBook(@PathVariable Long borrowingId) {
            CustomResponseBody<DtoBorrowResponse> body = CustomResponseBody.ok(borrowingService.returnBook(borrowingId), "Book returned successfully");
            return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
        }
    }
