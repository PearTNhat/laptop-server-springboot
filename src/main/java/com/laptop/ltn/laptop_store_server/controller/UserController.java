package com.laptop.ltn.laptop_store_server.controller;


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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public ApiResponse<Object> getUserInfo() {
        return ApiResponse.builder()
                .data(userService.getUserInfo())
                .build();
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUser(
            @RequestParam Map<String, String> queryParams,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable =  PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "id"));
        if (sort != null) {
            pageable =  PageRequest.of(page, limit, Sort.by(sort.split(",")));
        }

        Page<User> users = userService.findAllWithFilters(queryParams, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("counts", users.getTotalElements());
        response.put("data", users.getContent());

        return ResponseEntity.ok(response);

    }


}
