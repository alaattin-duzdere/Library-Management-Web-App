package com.example.library_management.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final NotificationServiceTest notificationServiceTest;

    public TestController(NotificationServiceTest notificationServiceTest) {
        this.notificationServiceTest = notificationServiceTest;
    }

    @PostMapping("/test/notification")
    public String notificationTest(){
        return notificationServiceTest.testNotificationService();
    }
}
