package com.example.library_management.penalties.service.reminder;

import com.example.library_management.borrowing.model.Borrowing;

public interface IReminderStrategy {
    void sendOverdueReminders(Borrowing borrowing);
}
