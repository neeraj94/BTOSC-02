package com.rbac.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${server.port:8080}")
    private String serverPort;

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Email Verification - RBAC System");
            message.setText("Please click the following link to verify your email:\n" +
                    "http://localhost:" + serverPort + "/api/auth/verify-email?token=" + token + "\n\n" +
                    "This link will expire in 24 hours.");

            mailSender.send(message);
            logger.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}", toEmail, e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset - RBAC System");
            message.setText("Please click the following link to reset your password:\n" +
                    "http://localhost:" + serverPort + "/api/auth/reset-password?token=" + token + "\n\n" +
                    "This link will expire in 24 hours.");

            mailSender.send(message);
            logger.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", toEmail, e);
        }
    }
}