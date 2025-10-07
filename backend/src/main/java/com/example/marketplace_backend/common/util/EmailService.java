package com.example.marketplace_backend.common.util;

import com.example.marketplace_backend.exception.BaseException;
import com.example.marketplace_backend.exception.ErrorMessage;
import com.example.marketplace_backend.exception.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        try {
            log.warn("Sending verification email to: " + to);

            String subject = "Verify your account";
            String url = "http://localhost:8080/api/auth/verify?token=" + token;
            String message = "Click the link to verify your account: " + url;

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
        } catch (MailException e) {
            throw new BaseException(new ErrorMessage(MessageType.RECORD_NOT_FOUND,"Failed to send verification email"));
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            log.warn("Sending password reset email to: " + to);

            String subject = "Reset your password";
            String url = "http://10.155.186.105:8080/api/auth/reset-password-handle?token=" + token;
            String message = "Click the link to reset your password: " + url;

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(MessageType.EMAIL_SEND_ERROR,"Failed to send password reset email"));
        }
    }
}
