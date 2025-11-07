package com.example.library_management.user.service.notification;

public interface INotificationService {
    void sendVerificationNotification(String destination, String subject, String message);
    void sendPasswordResetNotification(String destination, String subject, String message);
}
