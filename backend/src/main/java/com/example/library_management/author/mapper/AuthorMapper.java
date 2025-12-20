package com.example.library_management.author.mapper;

import com.example.library_management.author.dto.DtoAuthorRequest;
import com.example.library_management.author.dto.DtoAuthorResponse;
import com.example.library_management.author.model.Author;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public DtoAuthorResponse AuthorToDtoAuthorResponse(Author author){
        DtoAuthorResponse dtoAuthorResponse = new DtoAuthorResponse();
        BeanUtils.copyProperties(author, dtoAuthorResponse);
        return dtoAuthorResponse;
    }

    public Author dtoAuthorRequestToAuthor(DtoAuthorRequest dtoAuthorRequest){
        Author author = new Author();
        author.setFirstName(dtoAuthorRequest.getFirstName());
        author.setLastName(dtoAuthorRequest.getLastName());
        return author;
    }
}
