// js/api.js

const Api = {
    BASE_URL: "http://localhost:8080", 

    fetch: async (endpoint, options = {}) => {
        const url = `${Api.BASE_URL}${endpoint}`;
        
        const headers = {
            "Content-Type": "application/json",
            ...options.headers,
        };

        const token = Auth.getAccessToken();
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }

        // FormData gönderiyorsak Content-Type'ı sil (tarayıcı otomatik ayarlar)
        if (options.body instanceof FormData) {
            delete headers['Content-Type'];
        }

        try {
            const response = await fetch(url, { ...options, headers });

            if (response.status === 401) {
                if (window.location.pathname !== '/login.html' && window.location.pathname !== '/register.html') { 
                    console.error("Yetkisiz (401). Oturum sonlandırılıyor.");
                    Auth.logout();
                    return { success: false, message: "Oturum süresi doldu" }; 
                }
            }
            
            if (response.status === 403) {
                console.error("Yasak (403). Yetkiniz yok.");
                alert("Bu işlem için yetkiniz bulunmamaktadır.");
                if (window.location.pathname !== '/index.html') {
                     window.location.href = '/index.html';
                }
                return { success: false, message: "Yasaklı Erişim" };
            }
            
            const contentType = response.headers.get("content-type");
            if (response.status === 204 || !contentType || !contentType.includes("application/json")) {
                try {
                    const textData = await response.text();
                    if(textData) return { success: true, data: JSON.parse(textData) };
                } catch(e) {}
                return { success: response.ok, data: null };
            }

            const data = await response.json(); 

            return data;

        } catch (error) {
            console.error(`API Hatası (${endpoint}):`, error);
            return { success: false, message: error.message};
        }
    },

    // --- Auth Endpoints ---
    login: (email, password) => {
        return Api.fetch("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({ email, password })
        });
    },
    register: (username, email, password) => {
        return Api.fetch("/api/auth/register", {
            method: "POST",
            body: JSON.stringify({ username, email, password })
        });
    },
    forgotPassword: (email) => {
         return Api.fetch("/api/auth/forgot-password", {
            method: "POST",
            body: JSON.stringify({ email })
        });
    },
    resendVerification: (email) => {
        return Api.fetch("/api/auth/resend-verification", {
            method: "POST",
            body: JSON.stringify({ email: email }) 
        });
    },
    resetPassword: (token, newPassword, confirmNewPassword) => {
        return Api.fetch("/api/auth/reset-password-submit", { 
            method: "POST",
            body: JSON.stringify({ token, newPassword, confirmNewPassword })
        });
    },
    logout: () => {
        return Api.fetch("/api/auth/logout", { method: "POST" });
    },

    // --- Author Endpoints ---
    getAuthors: (page = 0, size = 20, search = "") => {
        let url = `/api/author?page=${page}&size=${size}`;
        if (search) url += `&search=${encodeURIComponent(search)}`;
        return Api.fetch(url, { method: "GET" });
    },
    getAuthorById: (id) => {
        return Api.fetch(`/api/author/${id}`, { method: "GET" });
    },
    createAuthor: (authorData) => {
        return Api.fetch("/api/admin/author", {
            method: "POST",
            body: JSON.stringify(authorData)
        });
    },
    updateAuthor: (id, authorData) => {
        return Api.fetch(`/api/admin/author/${id}`, {
            method: "PUT",
            body: JSON.stringify(authorData)
        });
    },
    deleteAuthor: (id) => {
        return Api.fetch(`/api/admin/author/${id}`, { method: "DELETE" });
    },

    // --- Category Endpoints ---
    getCategories: (page = 0, size = 20, search = "") => {
        let url = `/api/categories?page=${page}&size=${size}`;
        if (search) url += `&search=${encodeURIComponent(search)}`;
        return Api.fetch(url, { method: "GET" });
    },
    getCategoryById: (id) => {
        return Api.fetch(`/api/categories/${id}`, { method: "GET" });
    },
    createCategory: (categoryData) => {
        return Api.fetch("/api/admin/categories", {
            method: "POST",
            body: JSON.stringify(categoryData)
        });
    },
    updateCategory: (id, categoryData) => {
        return Api.fetch(`/api/admin/categories/${id}`, {
            method: "PUT",
            body: JSON.stringify(categoryData)
        });
    },
    deleteCategory: (id) => {
        return Api.fetch(`/api/admin/categories/${id}`, { method: "DELETE" });
    },

    // --- Book Endpoints (GÜNCELLENDİ: Admin yolları değişti) ---
    getBooks: (page = 0, size = 12, search = "", categoryId = null, authorId = null) => {
        // BookController -> getAllBooks
        let url = `/api/books?page=${page}&size=${size}&sort=likeCount,desc`; 
        
        if (search) url += `&search=${encodeURIComponent(search)}`;
        if (categoryId) url += `&categoryId=${categoryId}`;
        if (authorId) url += `&authorId=${authorId}`;
        
        return Api.fetch(url, { method: "GET" });
    },
    getBookById: (bookId) => {
        return Api.fetch(`/api/books/${bookId}`, { method: "GET" }); 
    },
    // GÜNCELLENDİ: /api/books -> /api/admin/books
    createBook: (bookData) => { 
        return Api.fetch("/api/admin/books", { 
            method: "POST",
            body: JSON.stringify(bookData)
        });
    },
    // GÜNCELLENDİ: /api/books/{id} -> /api/admin/books/{id}
    updateBook: (id, bookData) => {
        return Api.fetch(`/api/admin/books/${id}`, {
            method: "PUT",
            body: JSON.stringify(bookData)
        });
    },
    // GÜNCELLENDİ: /api/books/{id} -> /api/admin/books/{id}
    deleteBook: (id) => {
        return Api.fetch(`/api/admin/books/${id}`, { method: "DELETE" });
    },
    // GÜNCELLENDİ: /{bookId}/upload-photo -> /api/admin/books/{bookId}/upload
    uploadBookImage: (bookId, formData) => {
        return Api.fetch(`/api/admin/books/${bookId}/upload`, { 
            method: "POST",
            body: formData 
        });
    },

    // --- Borrowing Endpoints (GÜNCELLENDİ: Admin yolu değişti) ---
    borrowBook: (bookId) => {
        return Api.fetch(`/api/borrowings/books/${bookId}`, { method: "POST" });
    },
    returnBook: (borrowingId) => {
        return Api.fetch(`/api/borrowings/${borrowingId}/return`, { method: "POST" });
    },
    getMyBorrowings: (page = 0, size = 10) => {
        return Api.fetch(`/api/borrowings/me?page=${page}&size=${size}&sort=id,desc`, { method: "GET" });
    },
    // GÜNCELLENDİ: /api/borrowings -> /api/admin/borrowings
    getBorrowings: (page = 0, size = 10, userId = null, bookId = null) => {
        let url = `/api/admin/borrowings?page=${page}&size=${size}&sort=id,desc`;
        if (userId) url += `&userId=${userId}`;
        if (bookId) url += `&bookId=${bookId}`;
        return Api.fetch(url, { method: "GET" });
    },

    // --- Penalty Endpoints (GÜNCELLENDİ: 'my-penalties' -> 'me') ---
    getMyPenalties: () => {
        // PenaltyController -> getMyPenalties -> @GetMapping("/api/penalties/me")
        return Api.fetch('/api/penalties/me', { method: "GET" });
    },
    payPenalty: (penaltyId, amount) => {
        return Api.fetch('/api/penalties/pay', {
            method: "POST",
            body: JSON.stringify({ penaltyId, amount })
        });
    },

    // --- Comment Endpoints ---
    getCommentsByBook: (bookId, page = 0, size = 10) => {
        return Api.fetch(`/api/comments/book/${bookId}?page=${page}&size=${size}&sort=createTime,desc`, { method: "GET" });
    },
    addComment: (commentData) => {
        return Api.fetch('/api/comments', {
            method: "POST",
            body: JSON.stringify(commentData)
        });
    },
    deleteComment: (commentId) => {
        return Api.fetch(`/api/comments/${commentId}`, { method: "DELETE" });
    },
    updateComment: (commentId, content) => {
        return Api.fetch(`/api/comments/${commentId}`, {
            method: "PUT",
            body: JSON.stringify({ content: content })
        });
    },
    getMyComments: (page = 0, size = 10) => {
        return Api.fetch(`/api/comments/my-comments?page=${page}&size=${size}&sort=createTime,desc`, { method: "GET" });
    },

    // --- LIKE ENDPOINTS ---
    toggleLike: (bookId) => {
        return Api.fetch(`/api/likes/${bookId}`, { method: "POST" });
    },
    isBookLiked: (bookId) => {
        return Api.fetch(`/api/likes/${bookId}/check`, { method: "GET" });
    },
    getLikeCount: (bookId) => {
        return Api.fetch(`/api/likes/${bookId}/count`, { method: "GET" });
    },
    getMyFavorites: (page = 0, size = 20) => {
        return Api.fetch(`/api/likes/my-favorites?page=${page}&size=${size}`, { method: "GET" });
    }
};