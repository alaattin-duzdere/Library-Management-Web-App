package com.example.library_management.comment.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DtoCommentResponse {

    private long commentId;

    private long bookId;

    private String content;

    private Date createTime;

    private Long userId;

    private String username;
}
