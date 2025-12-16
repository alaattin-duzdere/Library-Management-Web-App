package com.example.library_management.like.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.model.Book;
import com.example.library_management.like.controller.IBookLikeController;
import com.example.library_management.like.service.IBookLikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class BookLikeControllerImpl implements IBookLikeController {

    private final IBookLikeService bookLikeService;

    public BookLikeControllerImpl(IBookLikeService bookLikeService) {
        this.bookLikeService = bookLikeService;
    }

    @PostMapping("/{bookId}")
    public ResponseEntity<CustomResponseBody<Void>> toggleLike(@PathVariable Long bookId) {
        Long currentUserId = getCurrentUserId();

        bookLikeService.toggleLike(currentUserId, bookId);

        return ResponseEntity.ok(CustomResponseBody.ok(null, "Like operation successful"));
    }

    @GetMapping("/{bookId}/check")
    public ResponseEntity<CustomResponseBody<Boolean>> isBookLiked(@PathVariable Long bookId) {
        Long currentUserId = getCurrentUserId();
        boolean isLiked = bookLikeService.isBookLiked(currentUserId, bookId);

        return ResponseEntity.ok(CustomResponseBody.ok(isLiked, "Like status checked"));
    }

    @GetMapping("/{bookId}/count")
    public ResponseEntity<CustomResponseBody<Long>> getLikeCount(@PathVariable Long bookId) {
        Long count = bookLikeService.getLikeCount(bookId);
        return ResponseEntity.ok(CustomResponseBody.ok(count, "Like count retrieved"));
    }

    @GetMapping("/my-favorites")
    @Override
    public ResponseEntity<CustomResponseBody<Page<DtoBookResponse>>> getMyFavorites(Pageable pageable) {
        Page<DtoBookResponse> favorites = bookLikeService.getMyFavorites(getCurrentUserId(), pageable);
        CustomResponseBody<Page<DtoBookResponse>> ok = CustomResponseBody.ok(favorites, "Favorites retrieved successfully");
        return ResponseEntity.ok(ok);
    }

    // --- Yardımcı Metot ---
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal.toString());
    }
}
