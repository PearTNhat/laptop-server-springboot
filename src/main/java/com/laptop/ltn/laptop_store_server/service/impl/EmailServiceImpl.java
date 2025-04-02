// Java Program to Illustrate Creation Of
// Service implementation class
package com.laptop.ltn.laptop_store_server.service.impl;
// Importing required classes

import java.io.File;

import com.laptop.ltn.laptop_store_server.dto.request.EmailRequest;
import com.laptop.ltn.laptop_store_server.exception.AppException;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// Annotation
@Service
// Class
// Implementing EmailService interface
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    // Method 1
    // To send a simple email
    public String sendSimpleMail(EmailRequest details) {

        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            String html = "<h1> Verify your account </h1>\n" +
                    "<p>\n" +
                    "  <h1> Your OTP </h1>\n" +
                    "  <strong>" + details.getOtp() + "</strong>\n" +
                    "</p>\n" +
                    "<p>The OTP expires in 5 minutes</p>";

            mailMessage.setText(html);
            String subject = "[Digital Store] OTP to verify your account";
            mailMessage.setSubject(subject);

            // Sending the mail
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            throw new AppException(ErrorCode.SEND_MAIL_FAIL);
        }
    }
}
