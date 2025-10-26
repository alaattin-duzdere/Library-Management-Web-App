package com.example.library_management.category.service.impl;

import com.example.library_management.category.dto.DtoCategoryRequest;
import com.example.library_management.category.dto.DtoCategoryResponse;
import com.example.library_management.category.model.Category;
import com.example.library_management.category.repository.CategoryRepository;
import com.example.library_management.category.service.ICategoryService;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private DtoCategoryResponse categoryToDtoCategoryResponse(Category category){
        DtoCategoryResponse dtoCategoryResponse = new DtoCategoryResponse();
        BeanUtils.copyProperties(category, dtoCategoryResponse);
        return dtoCategoryResponse;
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

        return categoryToDtoCategoryResponse(save);
    }

    @Override
    public DtoCategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return categoryToDtoCategoryResponse(category);
    }

    @Override
    public List<DtoCategoryResponse> getAllCategories() {
        List<Category> all = categoryRepository.findAll();

        List<DtoCategoryResponse> responseList = new ArrayList<>();
        for (Category category : all) {
            responseList.add(categoryToDtoCategoryResponse(category));
        }
        return responseList;
    }

    @Override
    public DtoCategoryResponse updateCategory(Long id, DtoCategoryRequest input) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setCategoryName(input.getCategoryName());
        Category updatedCategory = categoryRepository.save(category);

        return categoryToDtoCategoryResponse(updatedCategory);
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
