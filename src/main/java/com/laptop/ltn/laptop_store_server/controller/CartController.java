package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Cart;
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
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    /**
     * Get current user's cart
     * 
     * @param jwt The JWT token containing user info
     * @return The cart details with summary
     */
    @GetMapping
    public ResponseEntity<?> getCart(@AuthenticationPrincipal Jwt jwt) {
        try {
            String userId = jwt.getSubject();
            Map<String, Object> cartDetails = cartService.getCartDetails(userId);
            return ResponseEntity.ok(cartDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error retrieving cart: " + e.getMessage()));
        }
    }

    /**
     * Add item to cart
     * 
     * @param jwt     The JWT token containing user info
     * @param request Request body containing productId, quantity, and optional
     *                color
     * @return The updated cart
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request) {
        try {
            String userId = jwt.getSubject();
            String productId = (String) request.get("productId");
            Integer quantity = (Integer) request.get("quantity");
            String color = (String) request.get("color");

            if (productId == null || quantity == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Product ID and quantity are required"));
            }

            Cart updatedCart = cartService.addItemToCart(userId, productId, quantity, color);
            return ResponseEntity.ok(updatedCart);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(Map.of(
                            "code", e.getErrorCode().getCode(),
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error adding item to cart: " + e.getMessage()));
        }
    }

    /**
     * Update cart item quantity
     * 
     * @param jwt     The JWT token containing user info
     * @param request Request body containing productId, quantity, and optional
     *                color
     * @return The updated cart
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request) {
        try {
            String userId = jwt.getSubject();
            String productId = (String) request.get("productId");
            Integer quantity = (Integer) request.get("quantity");
            String color = (String) request.get("color");

            if (productId == null || quantity == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Product ID and quantity are required"));
            }

            Cart updatedCart = cartService.updateCartItem(userId, productId, quantity, color);
            return ResponseEntity.ok(updatedCart);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(Map.of(
                            "code", e.getErrorCode().getCode(),
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating cart item: " + e.getMessage()));
        }
    }

    /**
     * Remove item from cart
     * 
     * @param jwt       The JWT token containing user info
     * @param productId The product ID to remove
     * @param color     Optional color specification
     * @return The updated cart
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String productId,
            @RequestParam(required = false) String color) {
        try {
            String userId = jwt.getSubject();
            Cart updatedCart = cartService.removeCartItem(userId, productId, color);
            return ResponseEntity.ok(updatedCart);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(Map.of(
                            "code", e.getErrorCode().getCode(),
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error removing cart item: " + e.getMessage()));
        }
    }

    /**
     * Clear cart (remove all items)
     * 
     * @param jwt The JWT token containing user info
     * @return The cleared cart
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal Jwt jwt) {
        try {
            String userId = jwt.getSubject();
            Cart clearedCart = cartService.clearCart(userId);
            return ResponseEntity.ok(clearedCart);
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(Map.of(
                            "code", e.getErrorCode().getCode(),
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error clearing cart: " + e.getMessage()));
        }
    }
}
