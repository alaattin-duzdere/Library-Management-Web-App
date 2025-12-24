
# 📚 Library Management System (Kütüphane Yönetim Sistemi)

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?logo=redis)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?logo=docker)

Modern, güvenli ve ölçeklenebilir bir **Kütüphane Yönetim Sistemi**. Bu proje, kullanıcıların kitapları keşfetmesini, ödünç almasını ve kütüphane süreçlerini dijital olarak yönetmesini sağlayan kapsamlı bir **Full-Stack web uygulamasıdır**.


* 🔐 **JWT + Redis Blacklist** ile güvenli ve stateless authentication
* ⚡ **Redis Cache** ile performans optimizasyonu
* 🧱 **Spring Boot 3 & Java 17** ile clean, katmanlı mimari
* 📦 **Docker-ready** altyapı
* 📊 **Pagination, RBAC, Async Mail, File Upload** gibi gerçek dünya senaryoları

---

## 🚀 Proje Hakkında

Bu uygulama, hem kütüphane yöneticileri (**Admin**) hem de üyeler (**User**) için geliştirilmiştir. Klasik kütüphane işlemlerini dijitalleştirirken, arka planda **Redis ile önbellekleme**, **JWT Blacklist** mekanizması ve **Docker** desteği gibi modern yazılım mimarisi pratiklerini barındırır.

RESTful API mimarisi üzerine kurulu Backend servisi, standartlaştırılmış API yanıtları (**Custom Response Wrapper**) ile frontend tarafına tutarlı ve öngörülebilir veriler sunar.

---

## ✨ Özellikler

### 🛡️ Güvenlik ve Kimlik Doğrulama

* **JWT & Security:** JSON Web Token tabanlı stateless kimlik doğrulama.
* **Token Blacklist (Redis):** Güvenli çıkış (Logout) işlemi için Redis tabanlı token karalisteye alma servisi. Kullanıcı çıkış yaptığında token süresi dolana kadar Redis'te saklanır ve geçersiz kılınır.
* **Rol Yönetimi:** Admin ve User bazlı yetkilendirme (RBAC).
* **Şifre İşlemleri:** E-posta ile güvenli şifre sıfırlama akışı.
* **Kullanıcı Doğrulama:** E-posta ile kullanıcı doğrulama.

### 📚 Kitap ve Kütüphane Yönetimi

* **Gelişmiş Kitap Yönetimi:** Kitap ekleme, güncelleme, silme.
* **Görsel Yönetimi:** Kitap kapakları için görsel yükleme desteği.
* **Sayfalı Listeleme (Pagination):** Kitaplar ve loglar gibi büyük veri setleri için sunucu taraflı sayfalandırma.
* **Arama ve Filtreleme:** Kitap arama ve filreleme özelliği

### 🔄 Ödünç, İade ve Ceza

* **Ödünç Alma:** Müsaitlik kontrolü ile kitap ödünç alma.
* **Otomatik Ceza:** İade tarihi geçen kitaplar için otomatik ceza hesaplama.
* **Bildirim Sistemi:** İade tarihi yaklaşan kitaplar için asenkron e-posta bildirimleri.

### 💬 Sosyal Etkileşim

* **Yorum ve Beğeni:** Kullanıcıların kitaplara yorum yapması ve favorilere eklemesi.

### 📷 Statik Kaynak Yönetimi (WebMvcConfig)

* **Kitap Görseli:** Kullanıcıların yüklediği görseller (kitap kapakları vb.), sunucu üzerinde fiziksel bir dizinde saklanır. `WebMvcConfig` sınıfı sayesinde `/media/images/**` istekleri doğrudan bu fiziksel dizine yönlendirilir. Bu yaklaşım sayesinde:

* Veritabanı gereksiz yükten kurtulur
* Görseller daha hızlı sunulur
* Uygulama ölçeklenebilirliği artar

---

## 🛠️ Teknolojiler ve Mimari

### Backend

* **Dil:** Java 17+
* **Framework:** Spring Boot 3.x
* **Veritabanı:** PostgreSQL
* **Cache & NoSQL:** Redis (Token Blacklist ve Önbellekleme)
* **Güvenlik:** Spring Security, JWT
* **ORM:** Spring Data JPA, Hibernate
* **Dosya Yönetimi:** Custom Resource Handler (Yerel dosya sistemi)
* **Araçlar:** Maven, Lombok, Docker

### Frontend

* **Diller:** HTML5, CSS3, JavaScript (Vanilla ES6+)
* **Yapı:** Fetch API, Modular JS Architecture

---

## 🏗️ Mimari Detaylar


Proje geliştirilirken sürdürülebilirlik ve genişletilebilirlik ön planda tutulmuştur. Kullanılan temel mimari yaklaşımlar şunlardır:

### 1. Strategy Design Pattern (Strateji Deseni)
Kullanıcı doğrulama ve şifre sıfırlama işlemleri, değişime açık olacak şekilde tasarlanmıştır. `UserService` katmanında **Strategy Pattern** kullanılarak bu işlemler soyutlanmıştır:
* **Verification Strategy:** Şu anda e-posta linki ile doğrulama (`LinkVerificationStrategy`) kullanılmaktadır. İstenildiği takdirde koda dokunmadan SMS veya OTP stratejileri eklenebilir.
* **Password Reset Strategy:** Şifre sıfırlama akışı da aynı esneklikte arayüzler (`IPasswordResetStrategy`) üzerinden yönetilir.

### 2. DTO (Data Transfer Object) Pattern
Veritabanı varlıkları (Entity) hiçbir zaman doğrudan istemciye (Client) açılmaz. Tüm veri alışverişi `Request` ve `Response` DTO'ları üzerinden yapılır.
### 3. Global Exception Handling
Tüm sistem hataları tek bir merkezden (`GlobalExceptionHandler`) yönetilir. İstemciye dönülen hatalar, backend içinde ne yaşanırsa yaşansın (Veritabanı hatası, yetki hatası vb.) her zaman standart ve anlaşılır bir JSON formatındadır.

### 4. Custom Response Wrapper
API tutarlılığını sağlamak adına tüm uç noktalar `CustomResponseBody<T>` yapısını kullanır. Frontend geliştiricisi her zaman aşağıdaki standart yapıyı bekler:


## ⚙️ Kurulum ve Çalıştırma

Projeyi tam fonksiyonlu çalıştırmak için **PostgreSQL** ve **Redis** servislerinin ayakta olması gerekmektedir.

### Ön Koşullar

* Java JDK 17+
* Maven
* Docker (Redis için önerilir)
* PostgreSQL

---

### 1. Projeyi Klonlayın

```bash
git clone https://github.com/kullaniciadi/library-management-web-app.git
cd library-management-web-app
```

---

### 2. Redis Kurulumu (Docker)

Token blacklist ve caching mekanizmasının çalışması için Redis gereklidir. Docker ile hızlıca ayağa kaldırmak için:

```bash
docker run -d --name library-redis -p 6379:6379 redis
```

Docker kullanmıyorsanız, Redis Server’ı yerel makinenize kurup başlatmanız gerekmektedir.

---

### 3. Veritabanı Ayarları

1. PostgreSQL’de `library_db` adında boş bir veritabanı oluşturun.
2. `backend/src/main/resources/application.properties` dosyasını düzenleyin:

```properties
# Veritabanı
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=postgres
spring.datasource.password=sifreniz

# Redis Ayarları
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Dosya Yükleme Yolu
file.upload-dir=C:/Users/Kullanici/Desktop/LibraryUploads
# Linux/Mac için örnek: /home/user/library-uploads

# Mail Ayarları
spring.mail.username=mailadresiniz@gmail.com
spring.mail.password=uygulama_sifresi
```

---

### 4. Başlangıç Verileri (Init SQL)

Proje içerisinde bulunan `init.sql` dosyası:

* Gerekli roller (**ADMIN**, **USER**)
* Varsayılan admin kullanıcısını

içerir.

➡️ Uygulamayı ilk kez çalıştırmadan önce bu SQL dosyasını veritabanınızda çalıştırmanız gerekmektedir.

---

### 5. Backend’i Çalıştırın

```bash
cd backend
mvn spring-boot:run
```

Backend servis varsayılan olarak **[http://localhost:8080](http://localhost:8080)** adresinde çalışır.

---

### 6. Frontend’i Çalıştırın

`frontend/` klasörü içindeki `index.html` dosyasını tarayıcıda açmanız yeterlidir.

> 💡 Daha iyi bir deneyim ve CORS sorunları yaşamamak için **VS Code Live Server** eklentisi ile açmanız önerilir.

---

## 📖 API Dokümantasyonu

* Fazlası için **API_STATUS_CODES.md** dosyasını inceleyebilirsiniz.
---

## 👨‍💻 Geliştirici

**Alaattin Düzdere**
GitHub: `@alaattin`
