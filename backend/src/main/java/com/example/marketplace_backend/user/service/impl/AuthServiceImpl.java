package com.example.marketplace_backend.user.service.impl;

import com.example.marketplace_backend.common.enums.Role;
import com.example.marketplace_backend.common.util.EmailService;
import com.example.marketplace_backend.exception.BaseException;
import com.example.marketplace_backend.exception.ErrorMessage;
import com.example.marketplace_backend.exception.MessageType;
import com.example.marketplace_backend.security.JwtService;
import com.example.marketplace_backend.user.dto.AuthRequest;
import com.example.marketplace_backend.user.dto.AuthResponse;
import com.example.marketplace_backend.user.dto.DtoUser;
import com.example.marketplace_backend.user.dto.LoginRequest;
import com.example.marketplace_backend.user.model.RefreshToken;
import com.example.marketplace_backend.user.model.User;
import com.example.marketplace_backend.user.model.VerificationToken;
import com.example.marketplace_backend.user.repository.RefreshTokenRepository;
import com.example.marketplace_backend.user.repository.UserRepository;
import com.example.marketplace_backend.user.repository.VerificationTokenRepository;
import com.example.marketplace_backend.user.service.IAuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    private final EmailService emailService;

    private final VerificationTokenRepository tokenRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtUtil;

    private final AuthenticationProvider authenticationProvider;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(EmailService emailService, VerificationTokenRepository tokenRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtUtil, AuthenticationProvider authenticationProvider, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationProvider = authenticationProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User createUser(LoginRequest loginRequest) {
        User user = new User();
        user.setUsername(loginRequest.getUsername());
        user.setCreateTime(new Date());
        user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        user.setRoles(List.of(Role.USER)); // Default role is USER
        user.setEmail(loginRequest.getEmail());
        return user;
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreateTime(new Date());
        refreshToken.setExpiredDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)); // 1 day expiration
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        return refreshToken;
    }

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }

    @Override
    public DtoUser register(LoginRequest loginRequest) {
        try {
            if (userRepository.findByEmail(loginRequest.getEmail()).isPresent()) {
                throw new BaseException(new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, "Bu e-posta adresi zaten kayıtlı: " + loginRequest.getEmail()));
            }
            User savedUser = userRepository.save(createUser(loginRequest));
            String token = generateVerificationToken(savedUser);
            emailService.sendVerificationEmail(loginRequest.getEmail(),token);

            DtoUser dtoUser = new DtoUser();
            BeanUtils.copyProperties(savedUser,dtoUser);
            dtoUser.setRole(savedUser.getRoles().getFirst());
            return dtoUser;
        }
        catch (DataAccessException e) {
            throw new BaseException(new ErrorMessage(MessageType.DATABASE_ACCESS_ERROR, "Veritabanı hatası oluştu"));
        }
    }

    @Override
    public AuthResponse login(AuthRequest input) {
        try {
            log.warn("Attempting to authenticate user: {}", input.getEmail());
            User user = userRepository.findByEmail(input.getEmail()).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMAIL_NOT_FOUND, "User not found with email: " + input.getEmail())));
            log.warn("User found: {} , {}", user.getEmail(), user.getUsername());
            log.warn("Password {}",passwordEncoder.matches(input.getPassword(),user.getPassword()));

            if (!user.isVerified()){
                throw new BaseException(new ErrorMessage(MessageType.USER_NOT_VERIFIED ,"Email not verified for user: " + input.getEmail()));
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), input.getPassword());
            authenticationProvider.authenticate(authenticationToken);

            log.warn("Authentication successful for user: {}", input.getEmail());

            String accessToken = jwtUtil.generateToken(user);
            RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

            return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken());
        } catch (AuthenticationException e) {
            throw new BaseException(new ErrorMessage(MessageType.USERNAME_OR_PASSWORD_INVALID, "Invalid username or password"));
        }catch (DataAccessException e) {
            throw new BaseException(new ErrorMessage(MessageType.DATABASE_ACCESS_ERROR, "Database error occurred"));
        }
    }
}
