package com.example.library_management.comment.mapper;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.comment.dto.DtoCommentRequest;
import com.example.library_management.comment.dto.DtoCommentResponse;
import com.example.library_management.comment.model.Comment;
import com.example.library_management.common.util.SecurityUtils;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommentMapper {

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    public CommentMapper(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Comment createCommentFromCommentRequest(DtoCommentRequest input){
        Comment comment = new Comment();
        comment.setContent(input.getContent());
        comment.setCreateTime(new Date());

        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        comment.setUser(user);

        Book book = bookRepository.findById(input.getBookId()).orElseThrow(() -> new ResourceNotFoundException("Book", "id", input.getBookId()));
        comment.setBook(book);

        return comment;
    }

    public DtoCommentResponse commentToDtoCommentResponse(Comment comment){
        DtoCommentResponse dtoCommentResponse = new DtoCommentResponse();
        dtoCommentResponse.setContent(comment.getContent());
        dtoCommentResponse.setCreateTime(comment.getCreateTime());
        dtoCommentResponse.setBookId(comment.getBook().getId());
        dtoCommentResponse.setCommentId(comment.getId());

        User user = comment.getUser();
        dtoCommentResponse.setUserId(user.getId());
        dtoCommentResponse.setUsername(user.getUsername());
        return dtoCommentResponse;
    }
}
