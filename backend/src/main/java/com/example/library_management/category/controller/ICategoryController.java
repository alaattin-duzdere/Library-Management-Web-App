package com.example.library_management.category.controller;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.category.dto.DtoCategoryRequest;
import com.example.library_management.category.dto.DtoCategoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public interface ICategoryController {
    ResponseEntity<CustomResponseBody<DtoCategoryResponse>> saveCategory(DtoCategoryRequest input);

    ResponseEntity<CustomResponseBody<List<DtoCategoryResponse>>> getAllCategories();

    ResponseEntity<CustomResponseBody<DtoCategoryResponse>> getCategoryById(Long id);

    ResponseEntity<CustomResponseBody<DtoCategoryResponse>> updateCategory(Long id, DtoCategoryRequest input);

    ResponseEntity<CustomResponseBody<Boolean>> deleteCategory(Long id);

}
