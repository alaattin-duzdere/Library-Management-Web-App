package com.example.library_management.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DtoUpdateCommentRequest {

    @NotNull
    private String content;
}
