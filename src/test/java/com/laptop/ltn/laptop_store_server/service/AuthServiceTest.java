package com.laptop.ltn.laptop_store_server.service;


import com.laptop.ltn.laptop_store_server.dto.request.EmailRequest;
import com.laptop.ltn.laptop_store_server.dto.request.FinalRegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.request.LoginRequest;
import com.laptop.ltn.laptop_store_server.dto.request.RegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.response.LoginResponse;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.AppException;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.mapper.UserMapper;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.impl.AuthenticateImpl;
import com.laptop.ltn.laptop_store_server.utils.JwtUtils;
import com.laptop.ltn.laptop_store_server.utils.OtpUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthenticateImpl authService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtils jwtUtils;
    @Mock
    UserMapper userMapper;
    @Mock
    OtpUtils otpUtils;
    @Mock
    private EmailService emailService;

    @Mock
    private ScheduledExecutorService scheduler;
    @Mock
    HttpServletResponse response;

    User user;
    UserResponse userResponse;
    LoginResponse loginResponse;
    LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("123456")
                .build();
        user = User.builder()
                ._id("123456")
                .email("user@example.com")
                .password("$2a$10$encryptedPassword")
                .firstName("John")
                .lastName("Smith")
                .isBlocked(false)
                .build();
        userResponse = UserResponse.builder()
                ._id("123456")
                .email("user@example.com")
                .firstName("John")
                .lastName("Smith")
                .build();



    }

    @Test
    void login_shouldReturnLoginResponse_whenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtUtils.generateToken(Mockito.eq(user.get_id()), Mockito.anyInt()))
                .thenReturn("mockAccessToken");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        // Act
        LoginResponse loginResponse = authService.login(loginRequest, response);

        // Assert
        assertNotNull(loginResponse);
        assertEquals("mockAccessToken", loginResponse.getAccessToken());
        assertEquals(userResponse, loginResponse.getUserData());
    }
    @Test
    void login_shouldThrowException_whenEmailIsInvalid() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () ->
                authService.login(loginRequest, response));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }
    @Test
    void login_shouldThrowException_whenPasswordIsIncorrect() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(false); // Sai mật khẩu

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () ->
                authService.login(loginRequest, response));
        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void register_ShouldThrowException_WhenUserExists() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(User.builder().build()));
        AppException exception = assertThrows(AppException.class, () -> authService.register(request));
        assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());
    }
    @Test
    void register_ShouldCreateNewUserAndSendEmail_WhenUserNotExists() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(otpUtils.generateOTP()).thenReturn(123456);
        when(userRepository.findByEmailRegex("test@example.com&123456")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        boolean result = authService.register(request);

        assertTrue(result);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendSimpleMail(any(EmailRequest.class));
        verify(scheduler).schedule(any(Runnable.class), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    void finalRegister_ShouldUpdateEmail_WhenOtpIsCorrect() {
        FinalRegisterRequest request = FinalRegisterRequest.builder()
                .email("test@example.com")
                .otp("123456")
                .build();

        String emailWithOtp = "test@example.com&123456";
        User user = User.builder()
                .email(emailWithOtp)
                .build();

        when(userRepository.findByEmail(emailWithOtp)).thenReturn(Optional.of(user));

        authService.finalRegister(request);

        assertEquals("test@example.com", user.getEmail());
        verify(userRepository).save(user);
    }
    @Test
    void finalRegister_ShouldThrowException_WhenOtpIsInvalidLength() {
        FinalRegisterRequest request = FinalRegisterRequest.builder()
                .email("test@example.com")
                .otp("123") // OTP ngắn hơn 6 ký tự
                .build();

        AppException exception = assertThrows(AppException.class, () -> {
            authService.finalRegister(request);
        });

        assertEquals(ErrorCode.OTP_INCORRECT, exception.getErrorCode());
    }


}
