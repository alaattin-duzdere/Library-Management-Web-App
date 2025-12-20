package com.example.library_management.book.service.impl;

import com.example.library_management.author.model.Author;
import com.example.library_management.author.repository.AuthorRepository;
import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.mapper.BookMapper;
import com.example.library_management.book.model.Book;
import com.example.library_management.book.repository.BookRepository;
import com.example.library_management.book.service.IBookService;
import com.example.library_management.borrowing.repository.BorrowingRepository;
import com.example.library_management.category.model.Category;
import com.example.library_management.category.repository.CategoryRepository;
import com.example.library_management.common.util.ImageUploadService;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.InvalidInputException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class BookServiceImpl implements IBookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final AuthorRepository authorRepository;

    private final CategoryRepository categoryRepository;

    private final ImageUploadService imageUploadService;

    private final BorrowingRepository borrowingRepository;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper, AuthorRepository authorRepository, CategoryRepository categoryRepository, ImageUploadService imageUploadService, BorrowingRepository borrowingRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.imageUploadService = imageUploadService;
        this.borrowingRepository = borrowingRepository;
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public DtoBookResponse saveBook(DtoBookRequest dtoBookRequest) {
        if (bookRepository.existsByIsbn(dtoBookRequest.getIsbn())){
            throw new ConflictException("Book", "isbn", dtoBookRequest.getIsbn());
        }

        Book savedBook = bookRepository.save(bookMapper.createBookFromDto(dtoBookRequest));
        return bookMapper.createDtoFromBook(savedBook);
    }

    @Override
    public DtoBookResponse uploadPhoto(Long bookId, MultipartFile file) {
        if (file.isEmpty()){
            throw new InvalidInputException();
        }
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "bookId", bookId));
        String imageUrl = imageUploadService.saveImage(file);
        book.setImageUrl(imageUrl);
        return bookMapper.createDtoFromBook(bookRepository.save(book));
    }

    @Override
    public DtoBookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        return bookMapper.createDtoFromBook(book);
    }

    @Override
    public DtoBookResponse getBookByIsbn(Long isbn) {
        Book book = bookRepository.findByIsbn(isbn).orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));
        return bookMapper.createDtoFromBook(book);
    }

//    @Override
//    public Page<DtoBookResponse> getAllBooks(Pageable pageable, String query) {
//        if (query!=null && !query.isEmpty()){
//            Page<Book> bookPage = bookRepository.findByTitleContainingIgnoreCase(query, pageable);
//            return bookPage.map(this::createDtoFromBook);
//        }
//        Page<Book> bookPage = bookRepository.findAll(pageable);
//        return bookPage.map(this::createDtoFromBook);
//    }

    @Override
    public Page<DtoBookResponse> getAllBooks(Pageable pageable, String search, Long categoryId, Long authorId) {
        Page<Book> bookPage = bookRepository.searchBooks(search, categoryId, authorId, pageable);
        return bookPage.map(book -> bookMapper.createDtoFromBook(book));
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
        return bookMapper.createDtoFromBook(bookRepository.save(book));
    }

    @Override
    public Boolean deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)){
            throw new ResourceNotFoundException("Book", "id", bookId);
        }

        borrowingRepository.findActiveByBookId(bookId).ifPresent( borrowing -> {throw new ConflictException("Cannot delete a book that is currently borrowed.");});

        bookRepository.deleteById(bookId);
        return true;
    }
}
