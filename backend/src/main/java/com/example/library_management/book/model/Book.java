package com.example.library_management.book.model;

import com.example.library_management.author.model.Author;
import com.example.library_management.category.model.Category;
import com.example.library_management.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Table(name = "book")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    @Column(name="title")
    private String title;

    @Column(name="isbn", unique = true)
    private Long isbn;

    @ManyToMany
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    private Set<Category> categories = new HashSet<>();

    @Column(name="number_of_pages")
    public int numberOfPages;
}
