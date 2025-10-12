package com.example.library_management.security;

import com.example.library_management.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "rWwQOOZMkONRJon+GjwWPN2XtScgqevZJtjU9biNzFo=";

    public String generateToken(UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if(!(userDetails instanceof User user)) {
            throw new IllegalArgumentException("UserDetails must be an instance of User");
        }

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key getKey(){
        byte[] decode = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decode);
    }

    public <T> T exportToken(String token, Function<Claims,T> claimsFunc){
        Claims claims = getClaims(token);
        return claimsFunc.apply(claims);
    }

    public Claims getClaims(String token){
        return  Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserIdByToken(String token){
        return exportToken(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token){
        Date expireDate = exportToken(token, Claims::getExpiration);
        return new Date().before(expireDate);
    }
}
