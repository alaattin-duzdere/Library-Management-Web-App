package com.example.library_management.user.service.verification;

import com.example.library_management.exceptions.auth.ExpiredTokenException;
import com.example.library_management.exceptions.auth.InvalidTokenException;
import com.example.library_management.user.model.User;
import com.example.library_management.user.model.VerificationToken;
import com.example.library_management.user.repository.UserRepository;
import com.example.library_management.user.repository.VerificationTokenRepository;
import com.example.library_management.user.service.notification.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service("linkVerification")
@RequiredArgsConstructor
public class LinkVerificationStrategy implements IVerificationStrategy {

    private final VerificationTokenRepository tokenRepository;
    private final INotificationService notificationService;
    private final UserRepository userRepository;

    @Value("${redirection.port}")
    private String redirectionPort;

    @Value("${verification.token.expiration-seconds}")
    private long verificationTokenExpirationSeconds;

    @Override
    public void sendVerification(User user) {
        String token = createToken(user);
        String subject = "Verify your account";
        String url = "http://localhost:8080/api/auth/verify?token=" + token;
        String message = "Click the link to verify your account: " + url;
        notificationService.sendVerificationNotification(user.getEmail(), subject, message);
    }

    @Override
    public RedirectView verify(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token).orElseThrow(() -> new InvalidTokenException());

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException();
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        String targetUrl = "http://"+redirectionPort+"/login.html?verifySuccessful=true";
        return new RedirectView(targetUrl);
    }

    private String createToken(User user) {
        Optional<VerificationToken> existingTokenOpt = tokenRepository.findByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken;

        if (existingTokenOpt.isPresent()) {
            verificationToken = existingTokenOpt.get();
            verificationToken.setToken(token);
            verificationToken.setExpiryDate(LocalDateTime.now().plusSeconds(verificationTokenExpirationSeconds));
        } else {
            verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiryDate(LocalDateTime.now().plusSeconds(verificationTokenExpirationSeconds));
        }

        tokenRepository.save(verificationToken);
        return token;
    }
}
