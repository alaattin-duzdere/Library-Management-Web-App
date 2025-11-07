package com.example.library_management.test;

import com.example.library_management.borrowing.model.Borrowing;
import com.example.library_management.borrowing.repository.BorrowingRepository;
import com.example.library_management.exceptions.server.EmailServiceException;
import com.example.library_management.penalties.service.reminder.IReminderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
class ReminderServiceTest {

    private final IReminderStrategy reminderStrategy;

    private final BorrowingRepository borrowingRepository;

    ReminderServiceTest(@Qualifier("emailReminder") IReminderStrategy reminderStrategy, BorrowingRepository borrowingRepository) {
        this.reminderStrategy = reminderStrategy;
        this.borrowingRepository = borrowingRepository;
    }

    public String TestofOverdueReminders() {
        List<Borrowing> overdueBorrowings = borrowingRepository.findOverdueAndNotReturned();
        log.warn("Found " + overdueBorrowings.size() + " overdue borrowings.");

        for (Borrowing borrowing : overdueBorrowings) {
            try {
                reminderStrategy.sendOverdueReminders(borrowing);
            } catch (Exception e) {
                log.error("Failed to send reminder for borrowing ID {}: {}", borrowing.getId(), e.getMessage());
                throw new EmailServiceException("There is a problem with email sending",e);
            }
        }
        return "Test successful";
    }
}
