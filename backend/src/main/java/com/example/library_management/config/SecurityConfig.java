package com.example.library_management.config;

import com.example.library_management.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationEntryPoint authEntryPoint;

    private final AccessDeniedHandler customAccesDeniedHandler;

    private final AuthenticationProvider authenticationProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(AuthenticationEntryPoint authEntryPoint, AccessDeniedHandler customAccesDeniedHandler, AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authEntryPoint = authEntryPoint;
        this.customAccesDeniedHandler = customAccesDeniedHandler;
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Value("${api.auth.register}")
    private String registerEndpoint ;

    @Value("${api.auth.login}")
    private String loginEndpoint ;

    @Value("${api.auth.verify}")
    private String verifyEndpoint ;

    @Value("${api.auth.refresh}")
    private String refreshEndpoint ;

    @Value("${api.auth.reset-password-handle}")
    private String resetPasswordHandlerEndpoint;

    @Value("${api.auth.forgot-password}")
    private String forgotPasswordEndpoint;

    @Value("${api.auth.reset-password-submit}")
    private String passwordResetEndpoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/auth/reset-password-submit",registerEndpoint,loginEndpoint,refreshEndpoint,verifyEndpoint,forgotPasswordEndpoint, resetPasswordHandlerEndpoint, passwordResetEndpoint).permitAll()
                        .requestMatchers("/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/refreshToken").permitAll() // Allow Swagger UI and API docs
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight requests
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(customAccesDeniedHandler) // Custom access denied handler for handling forbidden access (403)
                        .authenticationEntryPoint(authEntryPoint) // Custom authentication entry point for handling unauthorized access (401)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Add your allowed origins (CRITICAL)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://10.155.186.94:3000" // Your frontend IP:port
        ));

        // 2. Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // 3. Allow required methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 4. Allow credentials (if using cookies/sessions)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all paths
        return source;
    }
}
