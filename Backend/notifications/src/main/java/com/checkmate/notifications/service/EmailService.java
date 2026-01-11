package com.checkmate.notifications.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {


    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationEmail(String toEmailAddress, String VerificationCode, String userId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail@playcheckmate.net");
        message.setTo(toEmailAddress);
        message.setSubject("2FA Verification");
        message.setText("Click the link below to verify:\n\nhttp://localhost:5173/verify?token=" + VerificationCode + "&userId=" + userId);
        emailSender.send(message);
    }
}
