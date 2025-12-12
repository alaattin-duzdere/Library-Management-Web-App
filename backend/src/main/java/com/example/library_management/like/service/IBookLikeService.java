package com.example.library_management.like.service;

public interface IBookLikeService {

    void toggleLike(Long userId, Long bookId);

    Long getLikeCount(Long bookId);

    boolean isBookLiked(Long userId, Long bookId);
}
