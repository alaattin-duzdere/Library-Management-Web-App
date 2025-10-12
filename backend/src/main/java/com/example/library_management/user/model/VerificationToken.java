package com.example.library_management.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class VerificationToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private LocalDateTime expiryDate;
}
