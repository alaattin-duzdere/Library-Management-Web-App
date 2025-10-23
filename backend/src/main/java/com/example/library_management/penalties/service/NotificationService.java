package com.example.library_management.penalties.service;

import com.example.library_management.borrowing.model.Borrowing;
import com.example.library_management.borrowing.repository.BorrowingRepository;
import com.example.library_management.common.util.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final BorrowingRepository borrowingRepository;

    private final EmailService emailService;

    public NotificationService(BorrowingRepository borrowingRepository, EmailService emailService) {
        this.borrowingRepository = borrowingRepository;
        this.emailService = emailService;
    }

    // Her gece saat 02:00'de çalışır
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(readOnly = true)
    public void sendOverdueReminders() {
        List<Borrowing> overdueBorrowings = borrowingRepository.findOverdueAndNotReturned();

        log.warn("overdue-borrowings: " + overdueBorrowings.toString());
        for (Borrowing borrowing : overdueBorrowings) {
            emailService.sendLateReminderEmail(borrowing.getUser(), borrowing.getBook());
        }
    }
}
