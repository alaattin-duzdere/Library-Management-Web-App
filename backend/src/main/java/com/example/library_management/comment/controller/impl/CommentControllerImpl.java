package com.example.library_management.comment.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.comment.controller.ICommentController;
import com.example.library_management.comment.dto.DtoCommentRequest;
import com.example.library_management.comment.dto.DtoCommentResponse;
import com.example.library_management.comment.dto.DtoUpdateCommentRequest;
import com.example.library_management.comment.service.ICommentService;
import com.example.library_management.common.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentControllerImpl implements ICommentController{

    private final ICommentService commentService;

    public CommentControllerImpl(ICommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Override
    public ResponseEntity<CustomResponseBody<DtoCommentResponse>> saveComment(@Valid @RequestBody DtoCommentRequest input) {
        CustomResponseBody<DtoCommentResponse> body = CustomResponseBody.ok(commentService.saveComment(input), "Comment saved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/book/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<Page<DtoCommentResponse>>> getCommentByBookId(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable Long bookId) {
        CustomResponseBody<Page<DtoCommentResponse>> body = CustomResponseBody.ok(commentService.getCommentByBookId(pageable,bookId), "Comment get successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/my-comments")
    @Override
    public ResponseEntity<CustomResponseBody<Page<DtoCommentResponse>>> getMyComments(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        CustomResponseBody<Page<DtoCommentResponse>> body = CustomResponseBody.ok(commentService.getCommentByUserId(pageable,currentUserId), "Comment get successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PutMapping("/{commentId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoCommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody DtoUpdateCommentRequest newContent) {
        CustomResponseBody<DtoCommentResponse> body = CustomResponseBody.ok(commentService.updateComment(commentId,newContent.getContent()), "Comment updated successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @DeleteMapping("/{commentId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoCommentResponse>> deleteComment(@PathVariable Long commentId) {
        CustomResponseBody<DtoCommentResponse> body = CustomResponseBody.ok(commentService.deleteComment(commentId), "Comment deleted successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
