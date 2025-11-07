package com.example.library_management.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final ReminderServiceTest reminderServiceTest;

    public TestController(ReminderServiceTest reminderServiceTest) {
        this.reminderServiceTest = reminderServiceTest;
    }

    @PostMapping("/test/notification")
    public String notificationTest(){
        return reminderServiceTest.TestofOverdueReminders();
    }
}
