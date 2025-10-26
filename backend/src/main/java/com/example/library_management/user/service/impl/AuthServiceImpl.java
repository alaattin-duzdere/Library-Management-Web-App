package com.example.library_management.user.service.impl;

import com.example.library_management.common.enums.Role;
import com.example.library_management.common.util.EmailService;
import com.example.library_management.exceptions.auth.ExpiredTokenException;
import com.example.library_management.exceptions.auth.InvalidCredentialsException;
import com.example.library_management.exceptions.auth.InvalidTokenException;
import com.example.library_management.exceptions.auth.UserNotVerifiedException;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.exceptions.client.PasswordMismatchException;
import com.example.library_management.exceptions.server.DatabaseException;
import com.example.library_management.security.JwtService;
import com.example.library_management.user.dto.*;
import com.example.library_management.user.model.RefreshToken;
import com.example.library_management.user.model.User;
import com.example.library_management.user.model.VerificationToken;
import com.example.library_management.user.repository.RefreshTokenRepository;
import com.example.library_management.user.repository.UserRepository;
import com.example.library_management.user.repository.VerificationTokenRepository;
import com.example.library_management.user.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Value("${jwt.refresh-expiration-seconds}")
    private long jwtRefreshExpirationSeconds;

    @Value("${jwt.acces-expiration-seconds}")
    private long jwtAccessExpirationSeconds;

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

    private User createUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setCreateTime(new Date());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(List.of(Role.USER)); // Default role is USER
        user.setEmail(registerRequest.getEmail());
        return user;
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreateTime(new Date());
        refreshToken.setExpiredDate(new Date(System.currentTimeMillis() + jwtRefreshExpirationSeconds * 1000)); // 1 day expiration
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        return refreshToken;
    }

    private String generateVerificationToken(User user) {
        Optional<VerificationToken> existingToken = tokenRepository.findByUserId(user.getId());
        if (existingToken.isPresent()) {
            return existingToken.get().getToken();
        }
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }

    @Override
    public DtoUser register(RegisterRequest registerRequest) {
        try {
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                User user = userRepository.findByEmail(registerRequest.getEmail()).get();
                if (!user.isVerified()){
                    emailService.sendVerificationEmail(registerRequest.getEmail(),generateVerificationToken(user));
                    throw new UserNotVerifiedException(registerRequest.getEmail());
                }
                throw new ConflictException("User","email", registerRequest.getEmail());
            }
            User savedUser = userRepository.save(createUser(registerRequest));
            String token = generateVerificationToken(savedUser);
            emailService.sendVerificationEmail(registerRequest.getEmail(),token);

            DtoUser dtoUser = new DtoUser();
            BeanUtils.copyProperties(savedUser,dtoUser);
            dtoUser.setRole(savedUser.getRoles().getFirst());
            return dtoUser;
        }
        catch (DataAccessException e) {
            throw new DatabaseException("Could not register user due to a database issue.");
        }
    }

    @Override
    public LoginResponse login(LoginRequest input) {
        try {
            log.warn("Attempting to authenticate user: {}", input.getEmail());
            User user = userRepository.findByEmail(input.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User","email",input.getEmail()));
            log.warn("User found: {} , {}", user.getEmail(), user.getUsername());
            log.warn("Password {}",passwordEncoder.matches(input.getPassword(),user.getPassword()));

            if (!passwordEncoder.matches(input.getPassword(),user.getPassword())){
                throw new InvalidCredentialsException();
            }
            if (!user.isVerified()){
                throw new UserNotVerifiedException(input.getEmail());
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), input.getPassword());
            authenticationProvider.authenticate(authenticationToken);

            log.warn("Authentication successful for user: {}", input.getEmail());

            String accessToken = jwtUtil.generateToken(user);
            RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

            return new LoginResponse(accessToken, savedRefreshToken.getRefreshToken(),jwtAccessExpirationSeconds,jwtRefreshExpirationSeconds);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }catch (DataAccessException e) {
            throw new DatabaseException("Could not login user due to a database issue.");
        }
    }

    private boolean isValidRefreshToken(Date expiredDate) {
        return new Date().before(expiredDate);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest input) {
        Optional<RefreshToken> optRefreshToken = refreshTokenRepository.findByRefreshToken(input.getRefreshToken());

        if (optRefreshToken.isEmpty()) {
            throw new InvalidTokenException("Refresh token not found: " + input.getRefreshToken());
        }
        if (!isValidRefreshToken(optRefreshToken.get().getExpiredDate())){
            throw new ExpiredTokenException("Refresh token has expired: " + input.getRefreshToken());
        }

        User user = optRefreshToken.get().getUser();
        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);
        RefreshToken savedRefreshToken= refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, savedRefreshToken.getRefreshToken(),jwtAccessExpirationSeconds,jwtRefreshExpirationSeconds);
    }

    private String createPasswordResetToken(String email) {
        log.info("Creating password reset jwt for email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User","email",email));

        return jwtUtil.generateToken(user);
    }

    @Override
    public String forgotPassword(String email) {
        String token = createPasswordResetToken(email);
        emailService.sendPasswordResetEmail(email, token);
        return "Password reset email sent to " + email;
    }

    @Override
    public ResponseEntity<Void> handleResetPassword(String token) {
        String userIdByToken = jwtUtil.getUserIdByToken(token);
        if (userIdByToken == null) {
            log.warn("Invalid token, redirecting to frontend reset password page without token");
            String redirectUrl = "http://10.155.186.94:3000/reset-password";
            return ResponseEntity.status(302).header("Location", redirectUrl).build();
        }
        log.warn("Token is valid, redirecting to frontend reset password page");
        String redirectUrl = "http://10.155.186.94:3000/reset-password?token=" + token;

        return ResponseEntity.status(302).header("Location", redirectUrl).build();
    }

    @Override // submit
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.warn("Resetting password for this request: {}", resetPasswordRequest);

        String userId= jwtUtil.getUserIdByToken(resetPasswordRequest.getToken());

        // Token geçerliliğini kontrol et
        if (userId == null) {
            throw new InvalidTokenException("Invalid password reset token");
        }
        if (!jwtUtil.isTokenValid(resetPasswordRequest.getToken())) {
            throw new ExpiredTokenException("Password reset token has expired");
        }

        // Yeni şifre ve onay şifresinin eşleştiğini kontrol et
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmNewPassword())){
            throw new PasswordMismatchException();
        }

        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));

        // Şifreyi güncelle
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

        userRepository.save(user);
        log.warn("Password updated successfully : {}" ,resetPasswordRequest.getNewPassword() + " for user: " + user.getEmail());
        return "Password reset successful";
    }

}