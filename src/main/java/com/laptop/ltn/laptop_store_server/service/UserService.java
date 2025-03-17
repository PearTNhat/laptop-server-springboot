package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService {
    @PreAuthorize("hasRole('admin')")
    List<UserResponse> getAllUsers();

    UserResponse getUserInfo();
}
