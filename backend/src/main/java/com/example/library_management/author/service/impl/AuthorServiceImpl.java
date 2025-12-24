package com.example.library_management.author.service.impl;

import com.example.library_management.author.dto.DtoAuthorRequest;
import com.example.library_management.author.dto.DtoAuthorResponse;
import com.example.library_management.author.mapper.AuthorMapper;
import com.example.library_management.author.model.Author;
import com.example.library_management.author.repository.AuthorRepository;
import com.example.library_management.author.service.IAuthorService;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AuthorServiceImpl implements IAuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public DtoAuthorResponse saveAuthor(DtoAuthorRequest input) {
        Author author = authorMapper.dtoAuthorRequestToAuthor(input);
        author.setCreateTime(new Date());

        Author savedAuthor = authorRepository.save(author);

        return authorMapper.AuthorToDtoAuthorResponse(savedAuthor);
    }

    @Override
    public DtoAuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author", " id", id));

        return authorMapper.AuthorToDtoAuthorResponse(author);
    }

    @Override
    public Page<DtoAuthorResponse> getAllAuthors(String query, Pageable pageable) {
        Page<Author> authors;
        if (query != null && !query.isEmpty()) {
            authors = authorRepository.findByFullNameContainingIgnoreCase(query, pageable);
        } else {
            authors = authorRepository.findAll(pageable);
        }
        return authors.map(author -> authorMapper.AuthorToDtoAuthorResponse(author));
    }

    @Override
    public DtoAuthorResponse updateAuthor(Long id, DtoAuthorRequest input) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author", " id", id));
        author.setFirstName(input.getFirstName());
        author.setLastName(input.getLastName());

        Author updatedAuthor = authorRepository.save(author);

        return authorMapper.AuthorToDtoAuthorResponse(updatedAuthor);
    }

    @Override
    public boolean deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)){
            throw new ResourceNotFoundException("Author", " id", id);
        }
        authorRepository.deleteById(id);
        return true;
    }
}
