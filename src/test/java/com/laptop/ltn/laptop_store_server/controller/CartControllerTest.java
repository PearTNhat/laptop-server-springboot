package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private CartController cartController;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                ._id("user1")
                .email("test@example.com")
                .build();

        // Setup JWT mock
        when(jwt.getSubject()).thenReturn("user1");
    }

    @Test
    @DisplayName("TCC-001: Update cart item should return success response")
    void TCC001_updateCartItem_WithValidRequest_ShouldReturnSuccessResponse() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("product", "product1");
        request.put("quantity", 3);
        request.put("color", "Black");

        doNothing().when(cartService).updateCartItem("user1", "product1", 3, "Black");

        // Act
        ApiResponse<Void> response = cartController.updateCartItem(jwt, request);

        // Assert
        assertNotNull(response);
        assertEquals("oke", response.getMessage());
        verify(cartService).updateCartItem("user1", "product1", 3, "Black");
    }

    @Test
    @DisplayName("TCC-002: Remove cart item should return updated user")
    void TCC002_removeCartItem_ShouldReturnUpdatedUser() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("product", "product1");
        request.put("color", "Black");

        when(cartService.removeCartItem("user1", "product1", "Black")).thenReturn(testUser);

        // Act
        ApiResponse<User> response = cartController.removeCartItem(jwt, request);

        // Assert
        assertNotNull(response);
        assertEquals(testUser, response.getData());
        verify(cartService).removeCartItem("user1", "product1", "Black");
    }
}
