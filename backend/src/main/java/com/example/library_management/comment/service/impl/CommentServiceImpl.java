package com.example.library_management.comment.service.impl;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.comment.dto.DtoCommentRequest;
import com.example.library_management.comment.dto.DtoCommentResponse;
import com.example.library_management.comment.mapper.CommentMapper;
import com.example.library_management.comment.model.Comment;
import com.example.library_management.comment.repository.CommentRepository;
import com.example.library_management.comment.service.ICommentService;
import com.example.library_management.common.enums.Role;
import com.example.library_management.common.util.SecurityUtils;
import com.example.library_management.exceptions.auth.ForbiddenException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentServiceImpl implements ICommentService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    public CommentServiceImpl(BookRepository bookRepository, UserRepository userRepository, CommentRepository commentRepository, CommentMapper commentMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    private boolean isCommentOwnerOrAdmin(Comment comment){
        Long userId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return comment.getUser().getId().equals(userId) || currentUser.getRoles().contains(Role.ADMIN);
    }

    @Override
    public DtoCommentResponse saveComment(DtoCommentRequest input) {
        Comment comment = commentMapper.createCommentFromCommentRequest(input);

        Comment savedComment = commentRepository.save(comment);

        Book book = savedComment.getBook();
        book.addComment(savedComment);

        return commentMapper.commentToDtoCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DtoCommentResponse> getCommentByBookId(Pageable pageable, Long bookId) {
        if (!bookRepository.existsById(bookId)){
            throw new ResourceNotFoundException("Book","id",bookId);
        }
        Page<Comment> commentPage = commentRepository.findByBookId(pageable,bookId);
        return commentPage.map(commentMapper::commentToDtoCommentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DtoCommentResponse> getCommentByUserId(Pageable pageable,Long userId) {
        if (!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User","id",userId);
        }
        Page<Comment> commentPage = commentRepository.findByUserId(pageable,userId);
        return commentPage.map(commentMapper::commentToDtoCommentResponse);
    }

    @Override
    public DtoCommentResponse updateComment(Long commentId,String newContent) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!isCommentOwnerOrAdmin(comment)){
            throw new ForbiddenException("You do not have permission to update this comment.");
        }

        comment.setContent(newContent);
        Comment save = commentRepository.save(comment);
        return commentMapper.commentToDtoCommentResponse(save);
    }

    @Override
    public DtoCommentResponse deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!isCommentOwnerOrAdmin(comment)){
            throw new ForbiddenException("You do not have permission to delete this comment.");
        }

        commentRepository.delete(comment);

        Book book = comment.getBook();
        book.removeComment(comment);

        return commentMapper.commentToDtoCommentResponse(comment);
    }
}
