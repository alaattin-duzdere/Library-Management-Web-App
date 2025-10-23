package com.example.library_management.book.service.impl;

import com.example.library_management.author.model.Author;
import com.example.library_management.author.repository.AuthorRepository;
import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.book.service.IBookService;
import com.example.library_management.category.model.Category;
import com.example.library_management.category.repository.CategoryRepository;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookServiceImpl implements IBookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final CategoryRepository categoryRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    private Book createBookFromDto(DtoBookRequest inputDto) {
        Book book = new Book();
        BeanUtils.copyProperties(inputDto, book);
        book.setCreateTime(new Date());

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

    private DtoBookResponse createDtoFromBook(Book book) {
        DtoBookResponse dtoBookResponse = new DtoBookResponse();
        BeanUtils.copyProperties(book, dtoBookResponse);

        Set<Author> authors = book.getAuthors();
        authors.forEach(author -> dtoBookResponse.getAuthors().add(author.getId()));

        Set<Category> categories = book.getCategories();
        categories.forEach(category -> dtoBookResponse.getCategories().add(category.getId()));

        return dtoBookResponse;
    }

    @Override
    public DtoBookResponse saveBook(DtoBookRequest dtoBookRequest) {
        if (bookRepository.existsByIsbn(dtoBookRequest.getIsbn())){
            throw new ConflictException("Book", "isbn", dtoBookRequest.getIsbn());
        }

        Book savedBook = bookRepository.save(createBookFromDto(dtoBookRequest));
        return createDtoFromBook(savedBook);
    }

    @Override
    public DtoBookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        return createDtoFromBook(book);
    }

    @Override
    public DtoBookResponse getBookByIsbn(Long isbn) {
        Book book = bookRepository.findByIsbn(isbn).orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));
        return createDtoFromBook(book);
    }

    @Override
    public List<DtoBookResponse> getAllBooks() {
        List<Book> all = bookRepository.findAll();
        return all.stream().map(this::createDtoFromBook).toList();
    }

    @Override
    public DtoBookResponse updateBook(Long bookId, DtoBookRequest dtoBookRequest) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        BeanUtils.copyProperties(dtoBookRequest,book);

        Set<Author> authors = new HashSet<>();
        for (Long authorId : dtoBookRequest.getAuthors()) {
            Author author = authorRepository.findById(authorId).orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));
            authors.add(author);
        }

        Set<Category> categories = new HashSet<>();
        for (Long categoryId : dtoBookRequest.getCategories()) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            categories.add(category);
        }

        book.setAuthors(authors);
        book.setCategories(categories);
        return createDtoFromBook(bookRepository.save(book));
    }

    @Override
    public Boolean deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)){
            throw new ResourceNotFoundException("Book", "id", bookId);
        }
        bookRepository.deleteById(bookId);
        return true;
    }
}
