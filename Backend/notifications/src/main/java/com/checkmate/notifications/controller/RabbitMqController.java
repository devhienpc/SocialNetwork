package com.checkmate.notifications.controller;

import com.checkmate.notifications.configuration.RabbitMqConfig;
import com.checkmate.notifications.model.dto.requests.VerificationMessage;
import com.checkmate.notifications.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RabbitMqController {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMqConfig.VERIFICATION_QUEUE)
    public void recieveVerificationEmailMessage(VerificationMessage message) {
        System.out.println("Message Recieved");
        System.out.println("Email: " + message.getEmail());
        System.out.println("Token: " + message.getToken());
        System.out.println("User ID: " + message.getUserId());
        // Send email
    }
}
