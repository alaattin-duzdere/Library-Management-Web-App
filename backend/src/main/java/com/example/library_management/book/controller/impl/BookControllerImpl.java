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

@RestController
public class BookControllerImpl implements IBookController {

    private final IBookService bookService;

    public BookControllerImpl(IBookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/api/admin/books")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> saveBook(@RequestBody @Valid DtoBookRequest dtoBookRequest) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.saveBook(dtoBookRequest), "Book created successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PostMapping("/api/admin/books/{bookId}/upload")
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
     * Kitapları listeler; arama, kategori/yazar filtreleme ve sayfalama özelliklerini destekler.
     * İstemci, bu parametrelerin herhangi bir kombinasyonunu kullanarak sonuçları daraltabilir.
     *
     * <p><strong>Kullanım Senaryoları:</strong></p>
     * <ul>
     * <li><strong>Tüm kitapları getir (Varsayılan):</strong> <br>
     * <code>GET /api/books</code></li>
     *
     * <li><strong>Genel Arama (Başlık, ISBN veya Yazar Adı):</strong> <br>
     * <code>GET /api/books?search=Yüzüklerin Efendisi</code></li>
     *
     * <li><strong>Belirli bir kategoriye göre filtrele:</strong> <br>
     * <code>GET /api/books?categoryId=5</code></li>
     *
     * <li><strong>Belirli bir yazara göre filtrele:</strong> <br>
     * <code>GET /api/books?authorId=12</code></li>
     *
     * <li><strong>Çoklu Filtreleme (Bilim Kurgu kategorisindeki, "Dune" içeren kitaplar):</strong> <br>
     * <code>GET /api/books?categoryId=3&search=Dune</code></li>
     *
     * <li><strong>Sayfalama ve Sıralama (2. sayfa, 20 kayıt, fiyata göre artan):</strong> <br>
     * <code>GET /api/books?page=1&size=20&sort=price,asc</code></li>
     * </ul>
     *
     * @param pageable   Sayfa numarası (page), boyutu (size) ve sıralama (sort) bilgileri.
     * @param search     (Opsiyonel) Kitap başlığı, ISBN veya yazar isminde aranacak metin.
     * @param categoryId (Opsiyonel) Filtrelenecek kategorinin ID'si.
     * @param authorId   (Opsiyonel) Filtrelenecek yazarın ID'si.
     * @return Filtrelenmiş kitap listesi ve sayfa bilgilerini içeren JSON yanıtı.
     */
    @GetMapping("/api/books")
    @Override
    public ResponseEntity<CustomResponseBody<Page<DtoBookResponse>>> getAllBooks(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, // Default pagination
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "authorId", required = false) Long authorId
    ) {
        CustomResponseBody<Page<DtoBookResponse>> body = CustomResponseBody.ok(bookService.getAllBooks(pageable,search,categoryId,authorId), " All books retrieved successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @PutMapping("/api/admin/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<DtoBookResponse>> updateBook(@PathVariable Long bookId, @RequestBody @Valid DtoBookRequest dtoBookRequest) {
        CustomResponseBody<DtoBookResponse> body = CustomResponseBody.ok(bookService.updateBook(bookId,dtoBookRequest), " Book updated successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }

    @DeleteMapping("/api/admin/books/{bookId}")
    @Override
    public ResponseEntity<CustomResponseBody<Boolean>> deleteBook(@PathVariable Long bookId) {
        CustomResponseBody<Boolean> body = CustomResponseBody.ok(bookService.deleteBook(bookId), " Book deleted successfully");
        return new ResponseEntity<>(body, HttpStatusCode.valueOf(body.getHttpStatus()));
    }
}
