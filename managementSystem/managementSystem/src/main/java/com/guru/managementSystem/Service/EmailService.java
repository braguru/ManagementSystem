package com.guru.managementSystem.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        // Construct your verification email content
        String subject = "Account Verification";
        String body = "Click the following link to verify your account: http://127.0.0.1:8080/api/v1/auth/verify?token=" + token +
                "<br><p>Link expires in a day.</p>";
        sendEmail(toEmail, subject, body);
    }

    @Async
    private void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setTo(toEmail);
        message.setText(body);

        mailSender.send(message);

    }
}
