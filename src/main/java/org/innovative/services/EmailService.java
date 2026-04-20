package org.innovative.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base.url}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("innovativequotesystem@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Verify your Innovative Quote Builder account");
        message.setText(
                "Welcome to Innovative Quote Builder!\n\n" +
                        "Please click the link below to verify your email address:\n\n" +
                        baseUrl + "/verify?token=" + token + "\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you did not create an account, please ignore this email."
        );
        mailSender.send(message);
    }
}