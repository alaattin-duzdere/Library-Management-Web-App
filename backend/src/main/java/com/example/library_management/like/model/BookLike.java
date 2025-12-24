package com.example.library_management.like.model;

import com.example.library_management.book.model.Book;
import com.example.library_management.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "book_likes", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "book_id"})})
public class BookLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDateTime likedAt;

    public BookLike(User user, Book book) {
        this.user = user;
        this.book = book;
        this.likedAt = LocalDateTime.now();
    }
}
