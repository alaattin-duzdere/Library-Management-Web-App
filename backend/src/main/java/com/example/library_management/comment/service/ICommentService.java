package com.example.library_management.comment.service;

import com.example.library_management.comment.dto.DtoCommentRequest;
import com.example.library_management.comment.dto.DtoCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICommentService {
    DtoCommentResponse saveComment(DtoCommentRequest input);

    Page<DtoCommentResponse> getCommentByBookId(Pageable pageable,Long bookId);

    Page<DtoCommentResponse> getCommentByUserId(Pageable pageable,Long userId);

    DtoCommentResponse updateComment(Long commentId, String newContent);

    DtoCommentResponse deleteComment(Long commentId);
}
