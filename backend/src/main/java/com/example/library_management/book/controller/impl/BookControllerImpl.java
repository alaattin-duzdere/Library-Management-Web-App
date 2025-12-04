package com.example.library_management.book.controller.impl;

import com.example.library_management.api.CustomResponseBody;
import com.example.library_management.book.controller.IBookController;
import com.example.library_management.book.dto.DtoBookRequest;
import com.example.library_management.book.dto.DtoBookResponse;
import com.example.library_management.book.service.IBookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class BookControllerImpl implements IBookController {

    private final IBookService bookService;

    public BookControllerImpl(IBookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/api/books")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> saveBook(@RequestBody @Valid DtoBookRequest dtoBookRequest) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.saveBook(dtoBookRequest), "Book created successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("/{bookId}/upload-photo")
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> uploadPhoto(@PathVariable Long bookId, @RequestParam("file") MultipartFile file) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.uploadPhoto(bookId, file), "Image upload successfully");
        return new ResponseEntity<>(body,HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookById(@PathVariable Long bookId) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.getBookById(bookId), " Book retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @GetMapping("/api/books/isbn/{isbn}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> getBookByIsbn(@PathVariable Long isbn) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.getBookByIsbn(isbn), " Book retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    /**
     * <p><strong>Kullanım Örnekleri:</strong></p>
     * <ul>
     * <li><strong>Varsayılan (İlk 10 kitap):</strong> <br>
     * <code>GET /api/kitaplar</code></li>
     *
     * <li><strong>Belirli bir sayfa (2. sayfa):</strong> <br>
     * <code>GET /api/kitaplar?page=1</code> (Not: Sayfa indeksi 0'dan başlar)</li>
     *
     * <li><strong>Sayfa boyutunu değiştirme (Tek seferde 50 kitap):</strong> <br>
     * <code>GET /api/kitaplar?size=50</code></li>
     *
     * <li><strong>Sıralama (Fiyata göre azalan):</strong> <br>
     * <code>GET /api/kitaplar?sort=fiyat,desc</code></li>
     *
     * <li><strong>Karmaşık Sorgu (3. sayfa, 20 kayıt, isme göre artan):</strong> <br>
     * <code>GET /api/kitaplar?page=2&size=20&sort=baslik,asc</code></li>
     * </ul>
     */
    @GetMapping("/api/books")
    @Override
    public ResponseEntity<CustomResponseBody<Page<DtoBookResponse>>> getAllBooks(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) { // Default pagination
        CustomResponseBody<Page<DtoBookResponse>> body = CustomResponseBody.ok(bookService.getAllBooks(pageable), " All books retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PutMapping("/api/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> updateBook(@PathVariable Long bookId, @RequestBody @Valid DtoBookRequest dtoBookRequest) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.updateBook(bookId,dtoBookRequest), " Book updated successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @DeleteMapping("/api/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<Boolean>> deleteBook(@PathVariable Long bookId) {
        CustomResponseBody<Boolean> body = CustomResponseBody.ok(bookService.deleteBook(bookId), " Book deleted successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
