package com.example.library_management.author.repository;

import com.example.library_management.author.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE " +
            "LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(CONCAT(a.lastName, ' ', a.firstName)) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Author> findByFullNameContainingIgnoreCase(String query, Pageable pageable);
}
