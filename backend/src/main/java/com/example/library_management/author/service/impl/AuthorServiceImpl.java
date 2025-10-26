package com.example.library_management.author.service.impl;

import com.example.library_management.author.dto.DtoAuthorRequest;
import com.example.library_management.author.dto.DtoAuthorResponse;
import com.example.library_management.author.model.Author;
import com.example.library_management.author.repository.AuthorRepository;
import com.example.library_management.author.service.IAuthorService;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AuthorServiceImpl implements IAuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    private DtoAuthorResponse AuthorToDtoAuthorResponse(Author author){
        DtoAuthorResponse dtoAuthorResponse = new DtoAuthorResponse();
        BeanUtils.copyProperties(author, dtoAuthorResponse);
        return dtoAuthorResponse;
    }

    @Override
    public DtoAuthorResponse saveAuthor(DtoAuthorRequest input) {
        Author author = new Author();
        author.setFirstName(input.getFirstName());
        author.setLastName(input.getLastName());
        author.setCreateTime(new Date());

        Author savedAuthor = authorRepository.save(author);

        return AuthorToDtoAuthorResponse(savedAuthor);
    }

    @Override
    public DtoAuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author", " id", id));

        return AuthorToDtoAuthorResponse(author);
    }

    @Override
    public List<DtoAuthorResponse> getAllAuthors() {
        List<Author> all = authorRepository.findAll();

        List<DtoAuthorResponse> dtoAuthorResponses = new ArrayList<>();
        for (Author author : all) {
            dtoAuthorResponses.add(AuthorToDtoAuthorResponse(author));
        }
        return dtoAuthorResponses;
    }

    @Override
    public DtoAuthorResponse updateAuthor(Long id, DtoAuthorRequest input) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Author", " id", id));
        author.setFirstName(input.getFirstName());
        author.setLastName(input.getLastName());

        Author updatedAuthor = authorRepository.save(author);

        return AuthorToDtoAuthorResponse(updatedAuthor);
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
