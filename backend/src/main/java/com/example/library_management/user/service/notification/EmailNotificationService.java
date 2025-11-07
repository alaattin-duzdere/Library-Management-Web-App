package com.example.library_management.user.service.notification;

import com.example.library_management.common.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements INotificationService {

    private final EmailService emailService;

    @Override
    public void sendVerificationNotification(String to, String subject, String message) {
        emailService.sendEmail(to, subject, message);
    }

    @Override
    public void sendPasswordResetNotification(String to, String subject, String message) {
        emailService.sendEmail(to, subject, message);
    }
}
