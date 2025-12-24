package com.example.library_management.like.service;

import com.example.library_management.book.dto.DtoBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookLikeService {

    void toggleLike(Long userId, Long bookId);

    Long getLikeCount(Long bookId);

    boolean isBookLiked(Long userId, Long bookId);

    Page<DtoBookResponse> getMyFavorites(Long userId, Pageable pageable);
}
