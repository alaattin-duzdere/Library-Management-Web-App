package com.example.library_management.security;

import com.example.library_management.api.ApiStatus;
import com.example.library_management.api.CustomResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public AuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // 1. Standart response body'mizi oluşturuyoruz.
        CustomResponseBody<?> responseBody = CustomResponseBody.failure(ApiStatus.ERROR_UNAUTHORIZED, authException.getMessage());

        // 2. HTTP yanıtının durum kodunu ve içerik tipini ayarlıyoruz.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 3. ObjectMapper kullanarak nesnemizi JSON string'ine çeviriyoruz.
        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        // 4. Oluşturduğumuz JSON'ı yanıta yazıyoruz.
        response.getWriter().write(jsonResponse);
    }
}
