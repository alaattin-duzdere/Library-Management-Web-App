package com.example.library_management.book.mapper;

import com.example.library_management.author.model.Author;
import com.example.library_management.author.repository.AuthorRepository;
import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.model.Book;
import com.example.library_management.category.model.Category;
import com.example.library_management.category.repository.CategoryRepository;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class BookMapper {

    private final AuthorRepository authorRepository;

    private final CategoryRepository categoryRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public BookMapper(AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    public Book createBookFromDto(DtoBookRequest inputDto) {
        Book book = new Book();
        BeanUtils.copyProperties(inputDto, book);
        book.setCreateTime(new Date());
        book.setLikeCount(0L);

        Set<Long> authors = inputDto.getAuthors();
        authors.forEach( authorId -> {
            Author author = authorRepository.findById(authorId).orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));
            book.getAuthors().add(author);
        });

        Set<Long> categories = inputDto.getCategories();
        categories.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            book.getCategories().add(category);
        });

        return book;
    }

    public DtoBookResponse createDtoFromBook(Book book) {
        DtoBookResponse dtoBookResponse = new DtoBookResponse();
        BeanUtils.copyProperties(book, dtoBookResponse);

        Set<Author> authors = book.getAuthors();
        authors.forEach(author -> dtoBookResponse.getAuthors().add(author.getId()));

        Set<Category> categories = book.getCategories();
        categories.forEach(category -> dtoBookResponse.getCategories().add(category.getId()));

        if (book.getImageUrl()!=null){
            dtoBookResponse.setImageUrl(baseUrl + dtoBookResponse.getImageUrl());
        }

        return dtoBookResponse;
    }
}
