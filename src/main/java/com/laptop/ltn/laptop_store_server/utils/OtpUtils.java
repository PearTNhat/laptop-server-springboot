package com.laptop.ltn.laptop_store_server.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtils {
    private final Random random;

    public OtpUtils() {
        this.random = new Random();
    }

    public int generateOTP() {
        return 100000 + random.nextInt(900000); // Tạo OTP 6 chữ số
    }
}
