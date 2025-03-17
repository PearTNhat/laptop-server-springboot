package com.laptop.ltn.laptop_store_server.service.impl;

import com.laptop.ltn.laptop_store_server.dto.request.EmailRequest;
import com.laptop.ltn.laptop_store_server.dto.request.FinalRegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.request.LoginRequest;
import com.laptop.ltn.laptop_store_server.dto.request.RegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.response.LoginResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.AppException;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.mapper.UserMapper;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.AuthenticateService;
import com.laptop.ltn.laptop_store_server.service.EmailService;
import com.laptop.ltn.laptop_store_server.utils.JwtUtils;
import com.laptop.ltn.laptop_store_server.utils.OtpUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticateImpl implements AuthenticateService {
    UserRepository userRepository;
    OtpUtils otpUtils;
    EmailService emailService;
    PasswordEncoder passwordEncoder;
    JwtUtils jwtUtils;
    UserMapper userMapper;

    @NonFinal
    @Value("${jwt.valid-duration}")
    int VALIDATION_DURATION;
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    int REFRESHABLE_DURATION;

    // lam settimeout
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public Boolean register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        // Generate OTP
        Integer otp = otpUtils.generateOTP();
        String emailEdited = request.getEmail() + "&" + otp;

        User tempUser = userRepository.findByEmailRegex(emailEdited);
        if (tempUser != null) {
            tempUser.setEmail(emailEdited);
            tempUser.setFirstName(request.getFirstName());
            tempUser.setLastName(request.getLastName());
            tempUser.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(tempUser);
        } else {
            User newUser = new User();
            newUser.setEmail(emailEdited);
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(newUser);
        }
        scheduler.schedule(() -> {
            userRepository.deleteByEmail(emailEdited);
        }, 5, TimeUnit.MINUTES);

        EmailRequest emailRequest = EmailRequest.builder()
                .otp(otp)
                .recipient(request.getEmail())
                .build();

        emailService.sendSimpleMail(emailRequest);
        return true;
    }

    @Override
    public void finalRegister(FinalRegisterRequest request) {
        String emailWithOtp = request.getEmail() + "&" + request.getOtp();
        System.out.println(emailWithOtp);
        User user = userRepository.findByEmail(emailWithOtp).orElseThrow(() -> new AppException(ErrorCode.OTP_INCORRECT));
        user.setEmail(user.getEmail().split("&")[0]); // Lấy phần email thực
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        // Find user with populated carts
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        // Check if user is blocked
        if (user.isBlocked()) {
            throw new AppException(ErrorCode.BLOCKED);
        }
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Generate tokens
        String newRefreshToken = jwtUtils.generateToken(user.get_id().toString(), REFRESHABLE_DURATION);

        String accessToken = jwtUtils.generateToken(user.get_id().toString(), VALIDATION_DURATION);

        // Update refresh token
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        // Set refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(refreshTokenCookie);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .userData(userMapper.toUserResponse(user))
                .build();

    }


}
