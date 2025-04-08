package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

public interface UserService {
    @PreAuthorize("hasAuthority('admin')")
    List<UserResponse> getAllUsers();

    UserResponse getUserInfo();
    Page<User> findAllWithFilters(Map<String, String> queryParams, Pageable pageable);
}
