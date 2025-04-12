package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.dto.request.UpdateBlockRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateRoleRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UserUpdateRequest;
import com.laptop.ltn.laptop_store_server.dto.request.WishListRequest;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
//    @PreAuthorize("hasAuthority('admin')")
//    List<UserResponse> getAllUsers();
    UserResponse getUserInfo();
    Page<User> findAllWithFilters(Map<String, String> queryParams, Pageable pageable);
    UserResponse updateUser(String document, MultipartFile file);
    void updateWishlist(WishListRequest request);
    void updateRole( UpdateRoleRequest request);
    void updateBlock(UpdateBlockRequest request);
}
