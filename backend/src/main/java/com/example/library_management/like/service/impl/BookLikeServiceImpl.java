package com.example.library_management.like.service.impl;

import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.like.model.BookLike;
import com.example.library_management.like.repository.BookLikeRepository;
import com.example.library_management.like.service.IBookLikeService;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BookLikeServiceImpl implements IBookLikeService {

    private final BookLikeRepository bookLikeRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    public BookLikeServiceImpl(BookLikeRepository bookLikeRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.bookLikeRepository = bookLikeRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void toggleLike(Long userId, Long bookId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        if (bookLikeRepository.existsByUserIdAndBookId(userId, bookId)) {
            //Unlike
            bookLikeRepository.deleteByUserIdAndBookId(userId, bookId);
            book.decrementLikeCount();
        } else {
            // Like
            BookLike like = new BookLike(user, book);
            bookLikeRepository.save(like);
            book.incrementLikeCount();
        }
    }

    @Override
    public boolean isBookLiked(Long userId, Long bookId) {
        return bookLikeRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Override
    public Long getLikeCount(Long bookId) {
        return bookLikeRepository.countByBookId(bookId);
    }
}
