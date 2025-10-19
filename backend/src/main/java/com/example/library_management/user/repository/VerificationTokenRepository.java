package com.example.library_management.user.repository;

import com.example.library_management.user.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    public Optional<VerificationToken> findByToken(String token);

    public Optional<VerificationToken> findByUserId(Long userId);
}
