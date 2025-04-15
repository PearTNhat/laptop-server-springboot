package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.entity.Cart;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.CustomException;
import com.laptop.ltn.laptop_store_server.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    /**
     * Update cart item quantity
     *
     * @param jwt     The JWT token containing user info
     * @param request Request body containing productId, quantity, and optional
     *                color
     * @return The updated cart
     */
    @PutMapping
    public ApiResponse<Void> updateCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request) {
        String userId = jwt.getSubject();
        String productId = (String) request.get("product");
        Integer quantity = (Integer) request.get("quantity");
        String color = (String) request.get("color");
        cartService.updateCartItem(userId, productId, quantity, color);
        return ApiResponse.<Void>builder()
                .message("oke")
                .build();

    }

    @DeleteMapping
    public ApiResponse<User> removeCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request) {

        String userId = jwt.getSubject();
        String product = (String) request.get("product");
        String color = (String) request.get("color");
        return ApiResponse.<User>builder()
                .data(cartService.removeCartItem(userId, product, color))
                .build();

    }
}
