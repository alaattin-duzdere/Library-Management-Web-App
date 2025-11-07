package com.example.library_management.penalties.service.reminder;

import com.example.library_management.borrowing.model.Borrowing;
import com.example.library_management.common.util.EmailService;
import com.example.library_management.exceptions.server.EmailServiceException;
import com.example.library_management.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("emailReminder")
public class EmailReminderStrategy implements IReminderStrategy{

    private final EmailService emailService;

    public EmailReminderStrategy(EmailService emailService) {
        this.emailService = emailService;
    }

    private final String SUBJECT_OF_OVERDUE_EMAIL = "Kitap Gecikme Hatırlatması | Akıllı Kütüphane";

    @Override
    public void sendOverdueReminders(Borrowing overdueBorrowing) {
        log.warn("overdue-borrowings founded with id: {} and username:  {}",overdueBorrowing.getId(),overdueBorrowing.getUser().getUsername());
        User user = overdueBorrowing.getUser();
        emailService.sendEmail(user.getEmail(),SUBJECT_OF_OVERDUE_EMAIL,prepareOverdueEmailFromBorrowing(overdueBorrowing));
    }

    private String prepareOverdueEmailFromBorrowing(Borrowing borrowing){
        String bookTitle = borrowing.getBook().getTitle();
        User user = borrowing.getUser();
        if (bookTitle == null || user.getUsername()==null || user.getEmail()==null) {
            log.error("Kullanıcı veya kitap bilgileri eksik olduğu için e-posta gönderilemedi.");
            throw new EmailServiceException("Email cannot send. There is a null field");
        }

        return String.format(
                "Sayın %s,\n\n" +
                        "Akıllı Kütüphane sistemimizden ödünç almış olduğunuz \"%s\" adlı kitabın iade süresi geçmiştir.\n\n" +
                        "Olası cezai işlemlerin artmasını önlemek için kitabı en kısa sürede kütüphanemize iade etmenizi rica ederiz.\n\n" +
                        "Anlayışınız için teşekkür eder, iyi günler dileriz.\n\n" +
                        "Saygılarımızla,\n" +
                        "Akıllı Kütüphane Yönetimi",
                user.getUsername(),
                bookTitle
        );
    }
}
