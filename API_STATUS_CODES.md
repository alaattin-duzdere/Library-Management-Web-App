
# Backend Hata Kodları

Bu dosya, backend servisinden dönebilecek hata kodlarını ve açıklamalarını içerir.
Frontend tarafında kullanıcıya gösterilecek mesajların belirlenmesinde bu liste kullanılmalıdır.

---

## Hata Formatı

```json
{
  "httpStatus": 404,
  "code": 1004,
  "message": "kayıt bulunamadı"
}
```

---

## Hata Kodları Listesi

| Error Code | HTTP Status                 | Açıklama                        |
| ---------- | --------------------------- | ------------------------------- |
| **1001**   | 404 (NOT_FOUND)             | Email bulunamadı                |
| **1002**   | 409 (CONFLICT)              | Email zaten kayıtlı             |
| **1003**   | 409 (CONFLICT)              | Bu şifre zaten kullanılıyor     |
| **1004**   | 404 (NOT_FOUND)             | Kayıt bulunamadı                |
| **1005**   | 401 (UNAUTHORIZED)          | Token süresi dolmuş             |
| **1006**   | 404 (NOT_FOUND)             | Kullanıcı adı bulunamadı        |
| **1007**   | 401 (UNAUTHORIZED)          | Kullanıcı adı veya şifre hatalı |
| **1008**   | 404 (NOT_FOUND)             | Refresh token bulunamadı        |
| **1009**   | 401 (UNAUTHORIZED)          | Refresh token süresi dolmuş     |
| **1010**   | 401 (UNAUTHORIZED)          | Kimlik doğrulama hatası         |
| **1011**   | 403 (FORBIDDEN)             | Email/kullanıcı doğrulanmamış   |
| **2001**   | 500 (INTERNAL_SERVER_ERROR) | Veritabanı erişim hatası        |
| **3001**   | 500 (INTERNAL_SERVER_ERROR) | Email gönderme hatası           |
| **9999**   | 500 (INTERNAL_SERVER_ERROR) | Genel bir hata oluştu           |

---
