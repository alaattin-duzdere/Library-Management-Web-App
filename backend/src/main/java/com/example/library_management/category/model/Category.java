package com.example.library_management.category.model;

import jakarta.persistence.Entity;
import com.example.library_management.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Category extends BaseEntity {
    private String categoryName;
}
