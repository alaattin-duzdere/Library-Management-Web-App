package com.example.library_management.book.repository;

import com.example.library_management.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    boolean existsByIsbn(Long isbn);

    Optional<Book> findByIsbn(Long isbn);

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN b.authors a " +
            "LEFT JOIN b.categories c " +
            "WHERE " +
            // 1. Arama Filtresi (Search)
            "(:search IS NULL OR :search = '' OR " +
            "   LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   CAST(b.isbn AS string) LIKE CONCAT('%', :search, '%') OR " +
            "   LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND " +
            // 2. Kategori Filtresi
            "(:categoryId IS NULL OR c.id = :categoryId) " +
            "AND " +
            // 3. Yazar Filtresi
            "(:authorId IS NULL OR a.id = :authorId)")
    Page<Book> searchBooks(@Param("search") String search, @Param("categoryId") Long categoryId, @Param("authorId") Long authorId, Pageable pageable);
}
