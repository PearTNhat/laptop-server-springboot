package com.laptop.ltn.laptop_store_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.laptop.ltn.laptop_store_server.dto.request.FinalRegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.request.LoginRequest;
import com.laptop.ltn.laptop_store_server.dto.request.RegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.response.LoginResponse;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.exception.AppException;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.AuthenticateService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.ArrayList;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticateService authenticateService;

    LoginRequest loginRequest;
    LoginResponse loginResponse;
    UserResponse userResponse;

    RegisterRequest registerRequest;
    FinalRegisterRequest finalRegisterRequest;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("123456")
                .build();
        userResponse = UserResponse.builder()
                ._id("userId123")
                .firstName("John")
                .lastName("Doe")
                .avatar(new Image())
                .email("test@test.com")
                .phone("0123456789")
                .address("123 Main St")
                .wishlist(new ArrayList<>())
                .carts(new ArrayList<>())
                .role("USER")
                .isBlocked(false)
                .build();

        loginResponse = LoginResponse.builder()
                .accessToken("accessToken")
                .userData(userResponse)
                .build();

        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .build();

        finalRegisterRequest = FinalRegisterRequest.builder()
                .email("test@example.com")
                .otp("123456")
                .build();

    }

    //     cần xem lại
    @Test
    void login_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);
        // given
        // thêm cookies vào response
        Mockito.doAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            Cookie refreshTokenCookie = new Cookie("refreshToken", "mockedRefreshToken");
            response.addCookie(refreshTokenCookie);
            return loginResponse;
        }).when(authenticateService).login(Mockito.any(), Mockito.any());
        // Mock HttpServletResponse để kiểm tra các hành động trên response (cookie, headers, etc.)

        // Thực hiện POST request tới /auth/login
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Kiểm tra mã status HTTP là OK
                .andExpect(MockMvcResultMatchers.cookie().exists("refreshToken"))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(loginResponse)));  // Kiểm tra nội dung response

    }

    @Test
    void login_fail_invalid_password() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);
        Mockito.doThrow(new AppException(ErrorCode.UNAUTHENTICATED))
                .when(authenticateService).login(Mockito.any(), Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()) // 401 Unauthorized
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username or password are incorrect"));
    }

    @Test
    void login_fail_account_blocked() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(loginRequest);

        // Giả lập AuthenticateService ném lỗi "Account is blocked"
        Mockito.doThrow(new AppException(ErrorCode.BLOCKED))
                .when(authenticateService).login(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // 401 Unauthorized
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Your account has been blocked"));
    }

    @Test
    void login_fail_missing_email() throws Exception {
        LoginRequest invalidRequest = LoginRequest.builder()
                .password("123456")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Email is required"));
    }

    @Test
    void login_fail_missing_password() throws Exception {
        LoginRequest invalidRequest = LoginRequest.builder()
                .email("abc@gmail.com")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Password is required"));
    }

    // register
    @Test
    void register_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(registerRequest);
        // given
        // thêm cookies vào response
        Mockito.when(authenticateService.register(Mockito.any())).thenReturn(true);
        // Mock HttpServletResponse để kiểm tra các hành động trên response (cookie, headers, etc.)

        // Thực hiện POST request tới /auth/login
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Kiểm tra mã status HTTP là OK
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(true))
                .andExpect((MockMvcResultMatchers.jsonPath("message").value("Sent email successfully"))); // Kiểm tra nội dung response

    }

    @Test
    void register_fail_mail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        // Giả lập AuthenticateService ném lỗi "Account is blocked"
        Mockito.doThrow(new AppException(ErrorCode.SEND_MAIL_FAIL))
                .when(authenticateService).register(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // 401 Unauthorized
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Error while sending mail"));
    }

    @Test
    void register_fail_missing_email() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .password("123456")
                .build(); // Không có email

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email is required"));
    }

    @Test
    void register_fail_missing_firstName() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .lastName("Doe")
                .password("123456")
                .build(); // Không có firstName

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("FirstName is required"));
    }

    @Test
    void register_fail_missing_lastName() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .password("123456")
                .build(); // Không có lastName

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("LastName is required"));
    }

    @Test
    void register_fail_missing_password() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build(); // Không có password

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Password is required"));
    }

    @Test
    void register_fail_short_password() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("123") // Password dưới 6 ký tự
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Password must be at least 6 characters"));
    }

    //    finalRegister ///////////////////////////////
    @Test
    void finalRegister_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(finalRegisterRequest);
        // given
        // thêm cookies vào response
        Mockito.doNothing().when(authenticateService).finalRegister(Mockito.any());
        // Mock HttpServletResponse để kiểm tra các hành động trên response (cookie, headers, etc.)

        // Thực hiện POST request tới /auth/login
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/final-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Kiểm tra mã status HTTP là OK
                .andExpect(MockMvcResultMatchers.jsonPath("success").value(true))
                .andExpect((MockMvcResultMatchers.jsonPath("message").value("Register successfully"))); // Kiểm tra nội dung response

    }

    @Test
    void finalRegister_fail_missing_email() throws Exception {
        FinalRegisterRequest request = FinalRegisterRequest.builder()
                .otp("123456")
                .build(); // Không có email

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/final-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Email is required"));
    }

    @Test
    void finalRegister_fail_missing_otp() throws Exception {
        FinalRegisterRequest request = FinalRegisterRequest.builder()
                .email("test@example.com")
                .build(); // Không có OTP

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/final-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Otp is required"));
    }

    @Test
    void finalRegister_fail_invalid_otp_length() throws Exception {
        FinalRegisterRequest request = FinalRegisterRequest.builder()
                .email("test@example.com")
                .otp("12346") // OTP chỉ có 5 ký tự
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/final-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Otp must be exactly 6 characters"));
    }

    @Test
    void finalRegister_fail_exceed_otp_length() throws Exception {
        FinalRegisterRequest request = FinalRegisterRequest.builder()
                .email("test@example.com")
                .otp("123457999") // OTP có 7 ký tự
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/final-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Otp must be exactly 6 characters"));
    }


}
