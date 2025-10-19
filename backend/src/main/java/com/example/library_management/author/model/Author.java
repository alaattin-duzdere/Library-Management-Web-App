package com.example.library_management.author.model;

import com.example.library_management.common.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="author")
@Entity
public class Author extends BaseEntity {

    private String firstName;

    private String lastName;
}
