package com.example.library_management.user.service.verification;

import com.example.library_management.user.model.User;
import org.springframework.web.servlet.view.RedirectView;

public interface IVerificationStrategy {
    void sendVerification(User user);
    RedirectView verify(String token);
}
