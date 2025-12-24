package com.example.library_management.category.mapper;

import com.example.library_management.category.dto.DtoCategoryResponse;
import com.example.library_management.category.model.Category;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public DtoCategoryResponse categoryToDtoCategoryResponse(Category category){
        DtoCategoryResponse dtoCategoryResponse = new DtoCategoryResponse();
        BeanUtils.copyProperties(category, dtoCategoryResponse);
        return dtoCategoryResponse;
    }
}
