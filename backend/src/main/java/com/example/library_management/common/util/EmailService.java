package com.example.library_management.common.util;

import com.example.library_management.book.model.Book;
import com.example.library_management.exceptions.client.ResourceNotFoundException;
import com.example.library_management.exceptions.server.EmailServiceException;
import com.example.library_management.user.model.User;
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
            throw new EmailServiceException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            log.warn("Sending password reset email to: " + to);

            String subject = "Reset your password";
            String url = "http://localhost:8080/api/auth/reset-password-handle?token=" + token;
            String message = "Click the link to reset your password: " + url;

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);
        } catch (Exception e) {
            throw new EmailServiceException("Failed to send password reset email", e);
        }
    }

    /**
     * Kullanıcıya, iade süresi geçmiş bir kitap için hatırlatma e-postası gönderir.
     *
     * @param user Gecikmiş kitabı olan kullanıcı nesnesi.
     * @param book Gecikmiş olan kitap nesnesi.
     */
    public void sendLateReminderEmail(User user, Book book) {
        if (user == null || user.getEmail() == null || book == null || book.getTitle()==null) {
            log.error("Kullanıcı veya kitap bilgileri eksik olduğu için e-posta gönderilemedi.");
            throw new RuntimeException();
        }

        // Spring'in basit e-posta mesajı nesnesini oluşturuyoruz.
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Kitap Gecikme Hatırlatması | Akıllı Kütüphane");

        String message = String.format(
                "Sayın %s,\n\n" +
                        "Akıllı Kütüphane sistemimizden ödünç almış olduğunuz \"%s\" adlı kitabın iade süresi geçmiştir.\n\n" +
                        "Olası cezai işlemlerin artmasını önlemek için kitabı en kısa sürede kütüphanemize iade etmenizi rica ederiz.\n\n" +
                        "Anlayışınız için teşekkür eder, iyi günler dileriz.\n\n" +
                        "Saygılarımızla,\n" +
                        "Akıllı Kütüphane Yönetimi",
                user.getUsername(),
                book.getTitle()
        );

        mailMessage.setText(message);

        try {
            mailSender.send(mailMessage);
            log.info("{} adresine gecikme hatırlatma e-postası başarıyla gönderildi.", user.getEmail());
        } catch (MailException e) {
            log.error("{} adresine e-posta gönderilirken bir hata oluştu: {}", user.getEmail(), e.getMessage());
            throw new EmailServiceException("There is a problem with sending mail.",e);
        }
    }
}
