package com.laptop.ltn.laptop_store_server.controller;


import com.laptop.ltn.laptop_store_server.dto.request.UpdateBlockRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateRoleRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UserUpdateRequest;
import com.laptop.ltn.laptop_store_server.dto.request.WishListRequest;
import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ApiResponse<Object> getUserInfo() {
        return ApiResponse.builder()
                .data(userService.getUserInfo())
                .build();
    }

    @GetMapping("/get-users")
    public ResponseEntity<Map<String, Object>> getAllUser(
            @RequestParam Map<String, String> queryParams,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort
    ) {
        // Điều chỉnh page về zero-based (trừ 1 nếu page >= 1)
        int adjustedPage = page >= 1 ? page - 1 : 0;

        Pageable pageable = PageRequest.of(adjustedPage, limit, Sort.by(Sort.Direction.ASC, "id"));
        if (sort != null) {
            pageable = PageRequest.of(adjustedPage, limit, Sort.by(sort.split(",")));
        }

        Page<User> users = userService.findAllWithFilters(queryParams, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("counts", users.getTotalElements());
        response.put("data", users.getContent());

        return ResponseEntity.ok(response);

    }

    @PutMapping
    public ApiResponse<UserResponse> updateUser(
            @RequestParam("document") String documentJson,
            @RequestPart(value = "avatar", required = false) MultipartFile file
    ) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(documentJson, file))
                .build();
    }
    @PutMapping("/wish-list")
    public ApiResponse<Void> updateWishList(
            @RequestBody  WishListRequest request
            ) {
        userService.updateWishlist(request);
        return ApiResponse.<Void>builder()
                .message("Update wishlist successfully")
                .build();
    }
    @PutMapping("/admin")
    public ApiResponse<Void> updateUserByAdmin(
           @RequestBody UpdateRoleRequest request
    ) {
        userService.updateRole(request);
        return ApiResponse.<Void>builder()
                .message("Update user successfully")
                .build();
    }
    @PutMapping("/admin/block")
    public ApiResponse<Void> blockUser(
            @RequestBody UpdateBlockRequest request
    ) {
        userService.updateBlock(request);
        return ApiResponse.<Void>builder()
                .message("Block user successfully")
                .build();
    }

}
