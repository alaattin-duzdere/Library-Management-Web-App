package com.example.library_management.author.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.author.controller.IAuthorController;
import com.example.library_management.author.dto.DtoAuthorRequest;
import com.example.library_management.author.dto.DtoAuthorResponse;
import com.example.library_management.author.service.IAuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthorControllerImpl implements IAuthorController {

    private final IAuthorService authorService;

    public AuthorControllerImpl(IAuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/api/admin/author")
    @Override
    public ResponseEntity<CustomResponseBody<DtoAuthorResponse>> saveAuthor(@RequestBody @Valid DtoAuthorRequest input) {
        CustomResponseBody<DtoAuthorResponse> body = CustomResponseBody.ok(authorService.saveAuthor(input), "Author saved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/author/{id}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoAuthorResponse>> getAuthorById(@PathVariable Long id) {
        CustomResponseBody<DtoAuthorResponse> body = CustomResponseBody.ok(authorService.getAuthorById(id), "Author retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/author")
    @Override
    public ResponseEntity<CustomResponseBody<List<DtoAuthorResponse>>> getAllAuthors() {
        CustomResponseBody<List<DtoAuthorResponse>> body = CustomResponseBody.ok(authorService.getAllAuthors(), "Authors retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PutMapping("/api/admin/author/{id}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoAuthorResponse>> updateAuthor(@PathVariable Long id, @RequestBody @Valid DtoAuthorRequest input) {
        CustomResponseBody<DtoAuthorResponse> body = CustomResponseBody.ok(authorService.updateAuthor(id, input), "Author updated successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @DeleteMapping("/api/admin/author/{id}")
    @Override
    public ResponseEntity<CustomResponseBody<Boolean>> deleteAuthor(@PathVariable Long id) {
        CustomResponseBody<Boolean> body = CustomResponseBody.ok(authorService.deleteAuthor(id), "Author deleted successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}