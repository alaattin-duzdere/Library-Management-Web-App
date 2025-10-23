package com.example.library_management.test;

import com.example.library_management.penalties.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
class NotificationServiceTest {

    private final NotificationService notificationService;

    NotificationServiceTest(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public String testNotificationService(){
        notificationService.sendOverdueReminders();
        return "test is over";
    }
}
