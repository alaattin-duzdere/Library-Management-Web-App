package com.example.library_management.category.service;

import com.example.library_management.category.dto.DtoCategoryRequest;
import com.example.library_management.category.dto.DtoCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {

    DtoCategoryResponse saveCategory(DtoCategoryRequest input);

    DtoCategoryResponse getCategoryById(Long id);

    Page<DtoCategoryResponse> getAllCategories(String query, Pageable pageable);

    DtoCategoryResponse updateCategory(Long id, DtoCategoryRequest input);

    Boolean deleteCategory(Long id);

}
