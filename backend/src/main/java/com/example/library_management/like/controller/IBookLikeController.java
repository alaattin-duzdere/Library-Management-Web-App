package com.example.library_management.like.controller;

import com.example.library_management.api.CustomResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface IBookLikeController {

    ResponseEntity<CustomResponseBody<Void>> toggleLike(Long bookId);

    ResponseEntity<CustomResponseBody<Boolean>> isBookLiked(Long bookId);

    ResponseEntity<CustomResponseBody<Long>> getLikeCount(Long bookId);
}
