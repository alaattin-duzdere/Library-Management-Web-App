package com.example.library_management.like.service.impl;

import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.mapper.BookMapper;
import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.book.service.impl.BookServiceImpl;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.like.model.BookLike;
import com.example.library_management.like.repository.BookLikeRepository;
import com.example.library_management.like.service.IBookLikeService;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookLikeServiceImpl implements IBookLikeService {

    private final BookLikeRepository bookLikeRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookLikeServiceImpl(BookLikeRepository bookLikeRepository, UserRepository userRepository, BookRepository bookRepository, BookServiceImpl bookService, BookMapper bookMapper) {
        this.bookLikeRepository = bookLikeRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public void toggleLike(Long userId, Long bookId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        if (bookLikeRepository.existsByUserIdAndBookId(userId, bookId)) {
            //Unlike
            bookLikeRepository.deleteByUserIdAndBookId(userId, bookId);
            book.decrementLikeCount();
            bookRepository.save(book);
        } else {
            // Like
            BookLike like = new BookLike(user, book);
            bookLikeRepository.save(like);
            book.incrementLikeCount();
            bookRepository.save(book);
        }
    }

    @Override
    public boolean isBookLiked(Long userId, Long bookId) {
        return bookLikeRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Override
    public Page<DtoBookResponse> getMyFavorites(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Page<BookLike> bookLikes = bookLikeRepository.findByUserId(userId,pageable);
        return bookLikes.map(bookLike -> bookMapper.createDtoFromBook(bookLike.getBook()));
    }

    @Override
    public Long getLikeCount(Long bookId) {
        return bookLikeRepository.countByBookId(bookId);
    }
}
