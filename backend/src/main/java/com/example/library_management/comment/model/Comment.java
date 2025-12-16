package com.example.library_management.comment.model;

import com.example.library_management.book.model.Book;
import com.example.library_management.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;

@Entity
@Data
@Table(name = "comments")
@SQLDelete(sql = "UPDATE \"library-management\".comments SET deleted = true WHERE id = ?") // Soft delete implementation
@SQLRestriction("deleted = false") // Ensures that only non-deleted records are fetched
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "deleted")
    private boolean deleted = false;
}
