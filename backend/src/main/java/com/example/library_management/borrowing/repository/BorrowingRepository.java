package com.example.library_management.borrowing.repository;

import com.example.library_management.borrowing.model.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Optional<Borrowing> findByBookId(Long bookId);

    Optional<Borrowing> findByUserId(Long userId);

    @Query("SELECT b FROM Borrowing b WHERE b.returnDate IS NULL AND b.lastReturnDate < CURRENT_TIMESTAMP")
    List<Borrowing> findOverdueAndNotReturned();
}
