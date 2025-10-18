package com.example.library_management.security;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.api.CustomResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    // ObjectMapper'ı constructor injection ile alıyoruz.
    // Bu, Java nesnelerini JSON'a çevirmek için standart ve en iyi yoldur.
    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 1. Standart response body'mizi oluşturuyoruz.
        CustomResponseBody<?> responseBody = CustomResponseBody.failure(ApiStatus.ERROR_FORBIDDEN, accessDeniedException.getMessage());

        // 2. HTTP yanıtının durum kodunu ve içerik tipini ayarlıyoruz.
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 3. ObjectMapper kullanarak CustomResponseBody nesnemizi JSON string'ine çeviriyoruz.
        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        // 4. Oluşturduğumuz JSON'ı yanıta yazıyoruz.
        response.getWriter().write(jsonResponse);
    }
}
