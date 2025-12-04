// js/auth.js

const Auth = {
    saveTokens: (access, refresh) => {
        localStorage.setItem('accessToken', access);
        localStorage.setItem('refreshToken', refresh);
    },

    getAccessToken: () => {
        return localStorage.getItem('accessToken');
    },
    
    getRefreshToken: () => {
        return localStorage.getItem('refreshToken');
    },

    isLoggedIn: () => {
        return !!Auth.getAccessToken();
    },

    /**
     * Logout API'sini çağırır ve token'ları temizler.
     */
    logout: async () => {
        try {
            // Backend'e token'ı karalisteye alması için istek at
            await Api.logout(); 
        } catch (error) {
            console.error("Logout API hatası (yine de çıkış yapılıyor):", error);
        } finally {
            // API başarısız olsa bile client tarafından çıkış yap
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '/login.html';
        }
    },

    resendVerification: (email) => {
        return Api.fetch("/api/auth/resend-verification", {
            method: "POST",
            body: JSON.stringify({ email: email }) // ForgotPasswordRequest DTO'su ile aynı
        });
    },

    /**
     * Token payload'unu decode eder.
     */
    decodeToken: () => {
        try {
            const token = Auth.getAccessToken();
            if (!token) return null;
            const payloadBase64 = token.split('.')[1];
            const payloadJson = atob(payloadBase64); // Base64 decode
            return JSON.parse(payloadJson);
        } catch (error) {
            console.error("Token decode hatası:", error);
            Auth.logout(); // Bozuk token varsa çıkış yap
            return null;
        }
    },
    
    /**
     * Token içinden kullanıcı bilgilerini alır.
     * Backend JWTService'te 'userName' ve 'sub' (ID) olarak eklenmiş.
     */
    getUserInfo: () => {
        const payload = Auth.decodeToken();
        if (!payload) return null;
        return {
            userId: payload.sub, // 'sub' (subject) claim'i kullanıcı ID'sidir
            username: payload.userName,
            roles: payload.authorities || []
        };
    },

    /**
     * Kullanıcının belirli bir role sahip olup olmadığını kontrol eder.
     */
    hasRole: (role) => {
        const userInfo = Auth.getUserInfo();
        if (!userInfo) return false;
        return userInfo.roles.includes(role);
    }
};