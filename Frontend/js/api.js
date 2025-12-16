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

            if (!response.ok) {
                throw new Error(data.message || "Bilinmeyen bir sunucu hatası oluştu.");
            }

            return data; 

        } catch (error) {
            console.error(`API Hatası (${endpoint}):`, error);
            return { success: false, message: error.message };
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

    // --- Author Endpoints (GÜNCELLENDİ: Pagination ve Search) ---
    getAuthors: (page = 0, size = 20, search = "") => {
        let url = `/api/author?page=${page}&size=${size}`;
        if (search) url += `&search=${encodeURIComponent(search)}`;
        return Api.fetch(url, { method: "GET" });
    },
    // YENİ: Tek yazar çekmek için (Edit modunda lazım olacak)
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

    // --- Category Endpoints (GÜNCELLENDİ: Pagination ve Search) ---
    getCategories: (page = 0, size = 20, search = "") => {
        let url = `/api/categories?page=${page}&size=${size}`;
        if (search) url += `&search=${encodeURIComponent(search)}`;
        return Api.fetch(url, { method: "GET" });
    },
    // YENİ: Tek kategori çekmek için (Edit modunda lazım olacak)
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

    // --- Book Endpoints (Pagination + Search + Filter Destekli) ---
    getBooks: (page = 0, size = 12, search = "", categoryId = null, authorId = null) => {
        let url = `/api/books?page=${page}&size=${size}&sort=likeCount,desc`; 
        
        if (search) url += `&search=${encodeURIComponent(search)}`;
        if (categoryId) url += `&categoryId=${categoryId}`;
        if (authorId) url += `&authorId=${authorId}`;
        
        return Api.fetch(url, { method: "GET" });
    },
    getBookById: (bookId) => {
        return Api.fetch(`/api/books/${bookId}`, { method: "GET" }); 
    },
    createBook: (bookData) => { 
        return Api.fetch("/api/books", { 
            method: "POST",
            body: JSON.stringify(bookData)
        });
    },
    updateBook: (id, bookData) => {
        return Api.fetch(`/api/books/${id}`, {
            method: "PUT",
            body: JSON.stringify(bookData)
        });
    },
    deleteBook: (id) => {
        return Api.fetch(`/api/books/${id}`, { method: "DELETE" });
    },
    uploadBookImage: (bookId, formData) => {
        return Api.fetch(`/${bookId}/upload-photo`, { 
            method: "POST",
            body: formData 
        });
    },

    // --- Borrowing Endpoints ---
    borrowBook: (bookId) => {
        return Api.fetch(`/api/borrow/${bookId}`, { method: "POST" });
    },
    returnBook: (borrowingId) => {
        return Api.fetch(`/api/borrow/return/${borrowingId}`, { method: "POST" });
    },
    getMyBorrowings: (userId) => {
        return Api.fetch(`/api/borrow/user/${userId}`, { method: "GET" });
    },

    // --- Penalty Endpoints ---
    getMyPenalties: () => {
        return Api.fetch('/api/penalties/my-penalties', { method: "GET" });
    },
    payPenalty: (penaltyId, amount) => {
        return Api.fetch('/api/penalties/pay', {
            method: "POST",
            body: JSON.stringify({ penaltyId, amount })
        });
    },

    // Yorumları Getir (Sayfalı)
    getCommentsByBook: (bookId, page = 0, size = 10) => {
        return Api.fetch(`/api/comments/book/${bookId}?page=${page}&size=${size}&sort=createTime,desc`, { method: "GET" });
    },

    // Yorum Ekle
    addComment: (commentData) => {
        return Api.fetch('/api/comments', {
            method: "POST",
            body: JSON.stringify(commentData)
        });
    },

    // Yorum Sil (Opsiyonel - Kendi yorumunu silmek için)
    deleteComment: (commentId) => {
        return Api.fetch(`/api/comments/${commentId}`, { method: "DELETE" });
    },

    // Yorum Güncelle
    updateComment: (commentId, content) => {
        return Api.fetch(`/api/comments/${commentId}`, {
            method: "PUT",
            body: JSON.stringify({ content: content })
        });
    },

    // Kullanıcının kendi yorumlarını getirir
    getMyComments: (page = 0, size = 10) => {
        return Api.fetch(`/api/comments/my-comments?page=${page}&size=${size}&sort=createTime,desc`, { method: "GET" });
    },

    // --- BEĞENİ (LIKE) ENDPOINTLERİ ---
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