package com.example.library_management.comment.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.comment.dto.DtoCommentRequest;
import com.example.library_management.comment.dto.DtoCommentResponse;
import com.example.library_management.comment.dto.DtoUpdateCommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ICommentController {
    ResponseEntity<CustomResponseBody<DtoCommentResponse>> saveComment(DtoCommentRequest input);

    ResponseEntity<CustomResponseBody<Page<DtoCommentResponse>>> getCommentByBookId(Pageable pageable,Long bookId);

    ResponseEntity<CustomResponseBody<Page<DtoCommentResponse>>> getCommentByUserId(Pageable pageable, Long userId);

    ResponseEntity<CustomResponseBody<DtoCommentResponse>> updateComment(Long commentId, DtoUpdateCommentRequest newContent);

    ResponseEntity<CustomResponseBody<DtoCommentResponse>> deleteComment(Long commentId);
}
