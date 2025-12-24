package com.example.library_management.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoCommentRequest {

    @NotNull
    private String content;

    @NotNull
    private long bookId;
}
