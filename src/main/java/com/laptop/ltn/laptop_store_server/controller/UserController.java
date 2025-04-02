package com.laptop.ltn.laptop_store_server.controller;


import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.dto.response.UserResponse;
import com.laptop.ltn.laptop_store_server.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authorities: " + authentication.getAuthorities());
        System.out.println(authentication.getName());
        authentication
                .getAuthorities()
                .forEach(grantedAuthority -> System.out.println(grantedAuthority.getAuthority()));
         return ApiResponse.<List<UserResponse>>builder()
                 .data(userService.getAllUsers())
                 .build();
    }
    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public ApiResponse<Object> getUserInfo() {
        return ApiResponse.builder()
                .data(userService.getUserInfo())
                .build();
    }


}
