package com.example.library_management.category.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.category.controller.ICategoryController;
import com.example.library_management.category.dto.DtoCategoryRequest;
import com.example.library_management.category.dto.DtoCategoryResponse;
import com.example.library_management.category.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryControllerImpl implements ICategoryController {

    private final ICategoryService categoryService;

    public CategoryControllerImpl(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/api/admin/categories")
    @Override
    public ResponseEntity<CustomResponseBody<DtoCategoryResponse>> saveCategory(@RequestBody @Valid DtoCategoryRequest input) {
        CustomResponseBody<DtoCategoryResponse> body = CustomResponseBody.ok(categoryService.saveCategory(input), "Category created successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/categories")
    @Override
    public ResponseEntity<CustomResponseBody<List<DtoCategoryResponse>>> getAllCategories() {
        CustomResponseBody<List<DtoCategoryResponse>> body = CustomResponseBody.ok(categoryService.getAllCategories(), " Categories retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/categories/{id}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoCategoryResponse>> getCategoryById(@PathVariable Long id) {
        CustomResponseBody<DtoCategoryResponse> body = CustomResponseBody.ok(categoryService.getCategoryById(id), " Category retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PutMapping("/api/admin/categories/{id}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoCategoryResponse>> updateCategory(@PathVariable Long id, @RequestBody @Valid DtoCategoryRequest input) {
        CustomResponseBody<DtoCategoryResponse> body = CustomResponseBody.ok(categoryService.updateCategory(id, input), " Category updated successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    @Override
    public ResponseEntity<CustomResponseBody<Boolean>> deleteCategory(@PathVariable Long id) {
        CustomResponseBody<Boolean> body = CustomResponseBody.ok(categoryService.deleteCategory(id), "Category deleted successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
