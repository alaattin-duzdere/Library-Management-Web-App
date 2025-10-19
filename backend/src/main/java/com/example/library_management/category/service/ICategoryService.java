package com.example.library_management.category.service;

import com.example.library_management.category.dto.DtoCategoryRequest;
import com.example.library_management.category.dto.DtoCategoryResponse;

import java.util.List;

public interface ICategoryService {

    DtoCategoryResponse saveCategory(DtoCategoryRequest input);

    DtoCategoryResponse getCategoryById(Long id);

    List<DtoCategoryResponse> getAllCategories();

    DtoCategoryResponse updateCategory(Long id, DtoCategoryRequest input);

    Boolean deleteCategory(Long id);

}
