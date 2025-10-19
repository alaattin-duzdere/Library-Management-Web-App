package com.example.library_management.category.repository;

import com.example.library_management.category.model.Category;
import jakarta.persistence.Lob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByCategoryName(String categoryName);
}
