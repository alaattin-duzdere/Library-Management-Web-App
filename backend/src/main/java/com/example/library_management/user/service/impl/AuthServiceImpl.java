package com.example.library_management.user.service.impl;

import com.example.library_management.common.enums.Role;
import com.example.library_management.common.util.EmailService;
import com.example.library_management.exception.BaseException;
import com.example.library_management.exception.ErrorMessage;
import com.example.library_management.exception.MessageType;
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
    public DtoUser register(LoginRequest loginRequest) {
        try {
            if (userRepository.findByEmail(loginRequest.getEmail()).isPresent()) {
                User user = userRepository.findByEmail(loginRequest.getEmail()).get();
                if (!user.isVerified()){
                    emailService.sendVerificationEmail(loginRequest.getEmail(),generateVerificationToken(user));
                    throw new BaseException(new ErrorMessage(MessageType.EMAIL_NOT_VERIFIED, "Bu e-posta adresi doğrulanmamış: " + loginRequest.getEmail()));
                }
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

    private boolean isValidRefreshToken(Date expiredDate) {
        return new Date().before(expiredDate);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest input) {
        Optional<RefreshToken> optRefreshToken = refreshTokenRepository.findByRefreshToken(input.getRefreshToken());

        if (optRefreshToken.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_INVALID,input.getRefreshToken()));
        }
        if (!isValidRefreshToken(optRefreshToken.get().getExpiredDate())){
            throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_EXPIRED,input.getRefreshToken()));
        }

        User user = optRefreshToken.get().getUser();
        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);
        RefreshToken savedRefreshToken= refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken());
    }

    private String createPasswordResetToken(String email) {
        log.info("Creating password reset jwt for email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.EMAIL_NOT_FOUND, "User not found with email: " + email)));

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
            throw new BaseException(new ErrorMessage(MessageType.TOKEN_INVALID,"Invalid password reset token"));
        }
        if (!jwtUtil.isTokenValid(resetPasswordRequest.getToken())) {
            throw new BaseException(new ErrorMessage(MessageType.TOKEN_EXPIRED,"Password reset token has expired"));
        }

        // Yeni şifre ve onay şifresinin eşleştiğini kontrol et
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmNewPassword())){
            throw new BaseException(new ErrorMessage(MessageType.PASSWORDS_DO_NOT_MATCH,"Password do not match"));
        }

        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND,"User not found with id: " + userId)));

        // Şifreyi güncelle
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));

        userRepository.save(user);
        log.warn("Password updated successfully : {}" ,resetPasswordRequest.getNewPassword() + " for user: " + user.getEmail());
        return "Password reset successful";
    }

}