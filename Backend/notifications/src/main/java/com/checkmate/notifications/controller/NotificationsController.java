package com.checkmate.notifications.controller;

import com.checkmate.notifications.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    @Autowired
    private EmailService emailService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void test() {
        System.out.println("Gotten");
    }

}
