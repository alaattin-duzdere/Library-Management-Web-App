package com.example.library_management.category.service.impl;

import com.example.library_management.category.dto.DtoCategoryRequest;
import com.example.library_management.category.dto.DtoCategoryResponse;
import com.example.library_management.category.mapper.CategoryMapper;
import com.example.library_management.category.model.Category;
import com.example.library_management.category.repository.CategoryRepository;
import com.example.library_management.category.service.ICategoryService;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public DtoCategoryResponse saveCategory(DtoCategoryRequest input) {
        if (categoryRepository.existsByCategoryName(input.getCategoryName())){
            throw new ConflictException("Category","categoryName",input.getCategoryName());
        }

        Category category = new Category();
        category.setCategoryName(input.getCategoryName());
        category.setCreateTime(new Date());

        Category save = categoryRepository.save(category);

        return categoryMapper.categoryToDtoCategoryResponse(save);
    }

    @Override
    public DtoCategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return categoryMapper.categoryToDtoCategoryResponse(category);
    }

    @Override
    public Page<DtoCategoryResponse> getAllCategories(String query, Pageable pageable) {
        Page<Category> categories;
        if (query != null && !query.isEmpty()) {
            categories = categoryRepository.findByCategoryNameContainingIgnoreCase(query, pageable);
        } else {
            categories = categoryRepository.findAll(pageable);
        }
        return categories.map(categoryMapper::categoryToDtoCategoryResponse);
    }

    @Override
    public DtoCategoryResponse updateCategory(Long id, DtoCategoryRequest input) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setCategoryName(input.getCategoryName());
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.categoryToDtoCategoryResponse(updatedCategory);
    }

    @Override
    public Boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)){
            throw new ResourceNotFoundException("Category", "id", id);
        }
        categoryRepository.deleteById(id);
        return true;
    }
}
