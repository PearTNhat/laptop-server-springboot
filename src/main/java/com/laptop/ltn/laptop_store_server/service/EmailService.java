package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.dto.request.EmailRequest;

public interface EmailService {
    // Method
    // To send a simple email
    String sendSimpleMail(EmailRequest request);
}
