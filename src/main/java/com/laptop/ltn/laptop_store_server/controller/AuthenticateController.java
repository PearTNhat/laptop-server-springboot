package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.request.FinalRegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.request.LoginRequest;
import com.laptop.ltn.laptop_store_server.dto.request.RegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.response.ApiLoginResponse;
import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.dto.response.LoginResponse;
import com.laptop.ltn.laptop_store_server.service.AuthenticateService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticateController {

    private AuthenticateService authService;

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest request) {
        boolean result = authService.register(request);
        if (result) {
            return ApiResponse.builder()
                    .message("Sent email successfully")
                    .build();
        }
        return ApiResponse.builder()
                .message("failed to send email")
                .build();
    }
    @PostMapping("/final-register")
    public ApiResponse<?> finalRegister(@RequestBody @Valid FinalRegisterRequest request) {
        System.out.println(request.toString());
        authService.finalRegister(request);
        return ApiResponse.builder()
                .message("Register successfully")
                .build();
    }
    @PostMapping("/login")
    public ApiLoginResponse<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse result = authService.login(request,response);
        return ApiLoginResponse.builder()
                .message("Login successfully")
                .accessToken(result.getAccessToken())
                .userData(result.getUserData())
                .build();
    }

}
