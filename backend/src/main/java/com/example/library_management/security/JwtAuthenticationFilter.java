package com.example.library_management.security;

import com.example.library_management.exceptions.auth.ExpiredTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header== null){
            filterChain.doFilter(request,response);
            return;
        }
        String token;
        String userId;

        token = header.substring(7);

        try{
            userId = jwtService.getUserIdByToken(token);
            if(userId!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                logger.info("UserDetails loaded: " + userDetails);
                if (userDetails != null || jwtService.isTokenValid(token)) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = jwtService.exportToken(token, claims -> claims.get("authorities", List.class));

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authenticationToken.setDetails(userDetails);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("User authenticated: " + userId);
                    logger.info("User has authority: " + authenticationToken.getAuthorities());
                }
            }
        }
        catch (ExpiredJwtException ex){
            throw new ExpiredTokenException("JWT token has expired");
        }
        catch (Exception e){
            throw new ServerException("Internal server error during JWT processing",e);
        }
        filterChain.doFilter(request, response);
    }
}
