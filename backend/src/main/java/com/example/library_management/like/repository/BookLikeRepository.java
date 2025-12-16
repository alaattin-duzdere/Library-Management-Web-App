package com.example.library_management.like.repository;

import com.example.library_management.like.model.BookLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId); // Check if a user has liked a specific book

    long countByBookId(Long bookId); // Count likes for a specific book

    void deleteByUserIdAndBookId(Long userId, Long bookId); // Unlike a book

    @Query("SELECT bl FROM BookLike bl JOIN bl.book b WHERE bl.user.id = :userId AND b.situation <> 'DELETED'")
    Page<BookLike> findByUserId(Long userId, Pageable pageable);
}
