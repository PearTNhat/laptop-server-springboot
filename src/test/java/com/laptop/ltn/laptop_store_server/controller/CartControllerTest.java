package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Cart;
import com.laptop.ltn.laptop_store_server.entity.CartItem;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private CartController cartController;

    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        // Setup test user
        User testUser = new User();
        testUser.set_id("user1");

        // Setup test product
        testProduct = new Product();
        testProduct.set_id("product1");
        testProduct.setTitle("Test Product");
        testProduct.setPrice(1000);
        testProduct.setDiscountPrice(900.0);

        // Setup test cart item
        testCartItem = new CartItem();
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);
        testCartItem.setColor("Black");

        // Setup test cart
        testCart = new Cart();
        testCart.set_id("cart1");
        testCart.setUser(testUser);
        testCart.setItems(new ArrayList<>(List.of(testCartItem)));
        testCart.setTotalPrice(900.0);
        testCart.setCreatedAt(LocalDateTime.now());
        testCart.setUpdatedAt(LocalDateTime.now());

        // Setup JWT mock
        when(jwt.getSubject()).thenReturn("user1");
    }

    @Test
    void getCart_ShouldReturnCartDetails() {
        // Arrange
        Map<String, Object> cartDetails = new HashMap<>();
        cartDetails.put("cart", testCart);
        cartDetails.put("totalItems", 1);
        cartDetails.put("totalPrice", 900.0);
        when(cartService.getCartDetails("user1")).thenReturn(cartDetails);

        // Act
        ResponseEntity<?> response = cartController.getCart(jwt);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartDetails, response.getBody());
        verify(cartService).getCartDetails("user1");
    }

    @Test
    void addToCart_WithValidRequest_ShouldReturnUpdatedCart() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productId", "product1");
        request.put("quantity", 2);
        request.put("color", "Black");
        when(cartService.addItemToCart("user1", "product1", 2, "Black")).thenReturn(testCart);

        // Act
        ResponseEntity<?> response = cartController.addToCart(jwt, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(cartService).addItemToCart("user1", "product1", 2, "Black");
    }

    @Test
    void addToCart_WithMissingRequiredFields_ShouldReturnBadRequest() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productId", "product1");

        // Act
        ResponseEntity<?> response = cartController.addToCart(jwt, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product ID and quantity are required", body.get("message"));
        verify(cartService, never()).addItemToCart(any(), any(), anyInt(), any());
    }

    @Test
    void updateCartItem_WithValidRequest_ShouldReturnUpdatedCart() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productId", "product1");
        request.put("quantity", 3);
        request.put("color", "Black");
        when(cartService.updateCartItem("user1", "product1", 3, "Black")).thenReturn(testCart);

        // Act
        ResponseEntity<?> response = cartController.updateCartItem(jwt, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(cartService).updateCartItem("user1", "product1", 3, "Black");
    }

    @Test
    void updateCartItem_WithMissingRequiredFields_ShouldReturnBadRequest() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productId", "product1");

        // Act
        ResponseEntity<?> response = cartController.updateCartItem(jwt, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product ID and quantity are required", body.get("message"));
        verify(cartService, never()).updateCartItem(any(), any(), anyInt(), any());
    }

    @Test
    void removeCartItem_ShouldReturnUpdatedCart() {
        // Arrange
        when(cartService.removeCartItem("user1", "product1", "Black")).thenReturn(testCart);

        // Act
        ResponseEntity<?> response = cartController.removeCartItem(jwt, "product1", "Black");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(cartService).removeCartItem("user1", "product1", "Black");
    }

    @Test
    void clearCart_ShouldReturnClearedCart() {
        // Arrange
        when(cartService.clearCart("user1")).thenReturn(testCart);

        // Act
        ResponseEntity<?> response = cartController.clearCart(jwt);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCart, response.getBody());
        verify(cartService).clearCart("user1");
    }
}
