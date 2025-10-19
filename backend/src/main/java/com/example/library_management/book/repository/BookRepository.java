package com.example.library_management.book.repository;

import com.example.library_management.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {

    boolean existsByIsbn(Long isbn);

    Optional<Book> findByIsbn(Long isbn);
}
