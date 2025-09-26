package com.example.marketplace_backend.user.service.impl;

import com.example.marketplace_backend.common.enums.Role;
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
import com.example.marketplace_backend.user.repository.RefreshTokenRepository;
import com.example.marketplace_backend.user.repository.UserRepository;
import com.example.marketplace_backend.user.service.IAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtUtil;

    private final AuthenticationProvider authenticationProvider;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(RefreshTokenRepository refreshTokenRepository, JwtService jwtUtil, AuthenticationProvider authenticationProvider, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
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

    @Override
    public DtoUser register(LoginRequest loginRequest) {
        User savedUser = userRepository.save(createUser(loginRequest));

        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(savedUser,dtoUser);
        dtoUser.setRole(savedUser.getRoles().getFirst());
        return dtoUser;
    }

    @Override
    public AuthResponse login(AuthRequest input) {
        try{
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword());
            authenticationProvider.authenticate(authenticationToken);

            User user = userRepository.findByUsername(input.getUsername()).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.No_Record_Exist,"User not found : "+input.getUsername())));

            String accessToken = jwtUtil.generateToken(user);
            RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

            return new AuthResponse(accessToken,savedRefreshToken.getRefreshToken());

        }catch (Exception e){
            throw new BaseException(new ErrorMessage(MessageType.Authentication_Error,"Authentication failed for user: "+input.getUsername()));
        }
    }
}
