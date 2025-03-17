package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.dto.request.FinalRegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.request.LoginRequest;
import com.laptop.ltn.laptop_store_server.dto.request.RegisterRequest;
import com.laptop.ltn.laptop_store_server.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticateService {
    Boolean register(RegisterRequest registerRequest);

    void finalRegister(FinalRegisterRequest request);

    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);
}
