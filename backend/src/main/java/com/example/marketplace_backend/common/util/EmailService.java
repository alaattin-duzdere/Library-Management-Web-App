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
}
