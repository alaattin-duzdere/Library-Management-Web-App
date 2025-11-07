package com.example.library_management.security;

import com.example.library_management.exceptions.server.ServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String tokenId, long expirationMillis) {
        try {
            redisTemplate.opsForValue().set(tokenId,"blacklist", expirationMillis, TimeUnit.MILLISECONDS);
            log.warn("token added blacklist with ex.timems: {} and tokenID: {}",expirationMillis,tokenId);
        } catch (Exception e) {
            log.warn("Exc: "+ e.getMessage());
            throw new ServerErrorException("An error occurred into redis.",e);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}