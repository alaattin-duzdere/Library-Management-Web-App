package com.example.library_management.user.model;

import com.example.library_management.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class RefreshToken extends BaseEntity {

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expired_date")
    private Date expiredDate;

    @ManyToOne
    private User user;
}
