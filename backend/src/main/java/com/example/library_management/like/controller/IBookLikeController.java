package com.example.library_management.like.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IBookLikeController {

    ResponseEntity<CustomResponseBody<Void>> toggleLike(Long bookId);

    ResponseEntity<CustomResponseBody<Boolean>> isBookLiked(Long bookId);

    ResponseEntity<CustomResponseBody<Long>> getLikeCount(Long bookId);

    ResponseEntity<CustomResponseBody<Page<DtoBookResponse>>> getMyFavorites(Pageable pageable);
}
