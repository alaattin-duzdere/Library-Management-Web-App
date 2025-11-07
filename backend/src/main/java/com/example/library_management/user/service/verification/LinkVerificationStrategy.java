package com.example.library_management.user.service.verification;

import com.example.library_management.exceptions.auth.ExpiredTokenException;
import com.example.library_management.exceptions.auth.InvalidTokenException;
import com.example.library_management.user.model.User;
import com.example.library_management.user.model.VerificationToken;
import com.example.library_management.user.repository.UserRepository;
import com.example.library_management.user.repository.VerificationTokenRepository;
import com.example.library_management.user.service.notification.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service("linkVerification")
@RequiredArgsConstructor
public class LinkVerificationStrategy implements IVerificationStrategy {

    private final VerificationTokenRepository tokenRepository;
    private final INotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    public void sendVerification(User user) {
        String token = createToken(user);
        String subject = "Verify your account";
        String url = "http://localhost:8080/api/auth/verify?token=" + token;
        String message = "Click the link to verify your account: " + url;
        notificationService.sendVerificationNotification(user.getEmail(), subject, message);
    }

    @Override
    public String verify(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token).orElseThrow(() -> new InvalidTokenException());

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException();
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        return "User verified successfully";
    }

    private String createToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);
        return token;
    }
}
