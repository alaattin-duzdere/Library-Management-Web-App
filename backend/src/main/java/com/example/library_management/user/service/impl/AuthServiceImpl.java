package com.example.library_management.user.service.impl;

import com.example.library_management.common.enums.Role;
import com.example.library_management.exceptions.auth.ExpiredTokenException;
import com.example.library_management.exceptions.auth.InvalidCredentialsException;
import com.example.library_management.exceptions.auth.InvalidTokenException;
import com.example.library_management.exceptions.auth.UserNotVerifiedException;
import com.example.library_management.exceptions.client.ConflictException;
import com.example.library_management.exceptions.client.PasswordMismatchException;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.exceptions.server.DatabaseException;
import com.example.library_management.security.JwtAudienceConstants;
import com.example.library_management.security.JwtService;
import com.example.library_management.security.TokenBlacklistService;
import com.example.library_management.user.dto.*;
import com.example.library_management.user.model.RefreshToken;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.RefreshTokenRepository;
import com.example.library_management.user.repository.UserRepository;
import com.example.library_management.user.service.IAuthService;
import com.example.library_management.user.service.password.IPasswordResetStrategy;
import com.example.library_management.user.service.verification.IVerificationStrategy;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Value("${jwt.refresh-expiration-seconds}")
    private long jwtRefreshExpirationSeconds;

    @Value("${jwt.access-expiration-seconds}")
    private long jwtAccessExpirationSeconds;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtUtil;

    private final AuthenticationProvider authenticationProvider;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final IVerificationStrategy verificationStrategy;

    private final IPasswordResetStrategy passwordResetStrategy;

    private final TokenBlacklistService blacklistService;

    public AuthServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtService jwtUtil, AuthenticationProvider authenticationProvider, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, @Qualifier("linkVerification") IVerificationStrategy verificationStrategy, @Qualifier("linkPasswordReset") IPasswordResetStrategy passwordResetStrategy, TokenBlacklistService blacklistService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationProvider = authenticationProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationStrategy = verificationStrategy;
        this.passwordResetStrategy = passwordResetStrategy;
        this.blacklistService = blacklistService;
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
        refreshToken.setExpiredDate(new Date(System.currentTimeMillis() + jwtRefreshExpirationSeconds *1000));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        return refreshToken;
    }

    @Override
    public DtoUser register(RegisterRequest registerRequest) {
        try {
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                User user = userRepository.findByEmail(registerRequest.getEmail()).get();
                if (!user.isVerified()){
                    verificationStrategy.sendVerification(user);
                    throw new UserNotVerifiedException(registerRequest.getEmail());
                }
                throw new ConflictException("User","email", registerRequest.getEmail());
            }
            User savedUser = userRepository.save(createUser(registerRequest));
            verificationStrategy.sendVerification(savedUser);

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
    public String verifyUser(String token) {
        return verificationStrategy.verify(token);
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

            String accessToken = jwtUtil.generateAccessToken(user);
            RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

            return new LoginResponse(accessToken, savedRefreshToken.getRefreshToken(), jwtAccessExpirationSeconds, jwtRefreshExpirationSeconds);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }catch (DataAccessException e) {
            throw new DatabaseException("Could not login user due to a database issue.");
        }
    }

    @Override
    public String logout(String token) {
        long expirationMillis = jwtUtil.getRemainingExpirationMillis(token);
        log.warn("expiration time mss "+ expirationMillis);

        refreshTokenRepository.deleteByUserId(Long.parseLong(jwtUtil.getUserIdByToken(token)));

        blacklistService.blacklistToken(jwtUtil.exportToken(token,Claims::getId), expirationMillis);

        return "User logout successful";
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
        String accessToken = jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);
        RefreshToken savedRefreshToken= refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, savedRefreshToken.getRefreshToken(), jwtAccessExpirationSeconds, jwtRefreshExpirationSeconds);
    }

    @Override
    public String forgotPassword(String email) {
        return passwordResetStrategy.sendResetToken(email);
    }

    @Override
    public ResponseEntity<Void> handleResetPassword(String token) {
        String userIdByToken = jwtUtil.getUserIdByToken(token);
        if (userIdByToken == null || !jwtUtil.getClaims(token).getAudience().equals(JwtAudienceConstants.RESET_PASS_TYPE)) {
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
        if (!jwtUtil.getClaims(resetPasswordRequest.getToken()).getAudience().equals(JwtAudienceConstants.RESET_PASS_TYPE)){
            throw new InvalidTokenException("The audience of provided token is not properly");
        }
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