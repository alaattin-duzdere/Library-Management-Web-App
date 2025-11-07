package com.example.library_management.user.service.password;

import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.security.JwtAudienceConstants;
import com.example.library_management.security.JwtService;
import com.example.library_management.user.model.User;
import com.example.library_management.user.repository.UserRepository;
import com.example.library_management.user.service.notification.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("linkPasswordReset")
@RequiredArgsConstructor
public class LinkPasswordResetStrategy implements IPasswordResetStrategy {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final INotificationService notificationService;

    @Override
    public String sendResetToken(String email) {
        log.info("Creating password reset token for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String token = jwtService.generateResetPassToken(user);

        log.warn("Sending password reset email to: {}", email);
        String subject = "Reset your password";
        String url = "http://localhost:8080/api/auth/reset-password-handle?token=" + token;
        String message = "Click the link to reset your password: " + url;

        notificationService.sendPasswordResetNotification(email, subject, message);

        return "Password reset instructions sent to " + email;
    }
}
