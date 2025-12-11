package com.example.library_management.comment.service.impl;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.comment.dto.DtoCommentRequest;
import com.example.library_management.comment.dto.DtoCommentResponse;
import com.example.library_management.comment.model.Comment;
import com.example.library_management.comment.repository.CommentRepository;
import com.example.library_management.comment.service.ICommentService;
import com.example.library_management.common.enums.Role;
import com.example.library_management.exceptions.auth.ForbiddenException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentServiceImpl implements ICommentService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    public CommentServiceImpl(BookRepository bookRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    private Long getUserIdFromContext(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal.toString());
    }

    private boolean isCommentOwnerOrAdmin(Comment comment){
        Long userId = getUserIdFromContext();
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return comment.getUser().getId().equals(userId) || currentUser.getRoles().contains(Role.ADMIN);
    }

    private Comment createCommentFromCommentRequest(DtoCommentRequest input){
        Comment comment = new Comment();
        comment.setContent(input.getContent());

        Long userId = getUserIdFromContext();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        comment.setUser(user);

        Book book = bookRepository.findById(input.getBookId()).orElseThrow(() -> new ResourceNotFoundException("Book", "id", input.getBookId()));
        comment.setBook(book);
        book.addComment(comment);

        return comment;
    }

    private DtoCommentResponse commentToDtoCommentResponse(Comment comment){
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

    @Override
    public DtoCommentResponse saveComment(DtoCommentRequest input) {
        Comment comment = createCommentFromCommentRequest(input);
        comment.setCreateTime(new Date());
        Comment save = commentRepository.save(comment);
        return commentToDtoCommentResponse(save);
    }

    @Override
    public Page<DtoCommentResponse> getCommentByBookId(Pageable pageable, Long bookId) {
        if (!bookRepository.existsById(bookId)){
            throw new ResourceNotFoundException("Book","id",bookId);
        }
        Page<Comment> commentPage = commentRepository.findByBookId(pageable,bookId);
        return commentPage.map(this::commentToDtoCommentResponse);
    }

    @Override
    public Page<DtoCommentResponse> getCommentByUserId(Pageable pageable,Long userId) {
        if (!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User","id",userId);
        }
        Page<Comment> commentPage = commentRepository.findByUserId(pageable,userId);
        return commentPage.map(this::commentToDtoCommentResponse);
    }

    @Override
    public DtoCommentResponse updateComment(Long commentId,String newContent) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!isCommentOwnerOrAdmin(comment)){
            throw new ForbiddenException("You do not have permission to update this comment.");
        }

        comment.setContent(newContent);
        Comment save = commentRepository.save(comment);
        return commentToDtoCommentResponse(save);
    }

    @Override
    public DtoCommentResponse deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!isCommentOwnerOrAdmin(comment)){
            throw new ForbiddenException("You do not have permission to delete this comment.");
        }

        comment.getBook().removeComment(comment);

        commentRepository.delete(comment);
        return commentToDtoCommentResponse(comment);
    }
}
