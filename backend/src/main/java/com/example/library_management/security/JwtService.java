package com.example.library_management.security;

import com.example.library_management.user.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.access-expiration-seconds}")
    private long jwtAccessExpirationSeconds;
    @Value("${jwt.reset-pass-expiration-seconds}")
    private long jwtResetPassExpirationSeconds;

    private static final String SECRET_KEY = "rWwQOOZMkONRJon+GjwWPN2XtScgqevZJtjU9biNzFo=";

    public String generateAccessToken(UserDetails userDetails) {
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
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpirationSeconds *1000)) //
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .claim("email", user.getEmail())
                .claim("userName", user.getUsername())
                .claim(Claims.AUDIENCE,JwtAudienceConstants.ACCESS_TOKEN_TYPE)
                .claim(Claims.ID, UUID.randomUUID().toString())
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateResetPassToken(UserDetails userDetails) {
        if(!(userDetails instanceof User user)) {
            throw new IllegalArgumentException("UserDetails must be an instance of User");
        }
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtResetPassExpirationSeconds *1000)) //
                .claim(Claims.AUDIENCE,JwtAudienceConstants.RESET_PASS_TYPE)
                .claim(Claims.ID, UUID.randomUUID().toString())
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

    public String getUserIdByToken(String token)throws ExpiredJwtException, MalformedJwtException, SignatureException{
        return exportToken(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token){
        Date expireDate = exportToken(token, Claims::getExpiration);
        return new Date().before(expireDate);
    }

    public long getRemainingExpirationMillis(String token){
        Date expireDate = exportToken(token, Claims::getExpiration);
        return (expireDate.getTime()-new Date().getTime());
    }
}
