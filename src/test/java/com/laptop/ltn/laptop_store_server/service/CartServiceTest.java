package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Cart;
import com.laptop.ltn.laptop_store_server.entity.CartItem;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.CustomException;
import com.laptop.ltn.laptop_store_server.repository.CartRepository;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceTest.class);

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;

    // Using the interface reference for tests
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                ._id("user123")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password")
                .role("user")
                .build();

        testProduct = Product.builder()
                ._id("prod123")
                .title("Test Laptop")
                .slug("test-laptop")
                .price(1000)
                .discountPrice(900.0)
                .quantity(10)
                .build();

        testCartItem = CartItem.builder()
                .product(testProduct)
                .quantity(2)
                .color("Black")
                .build();

        testCart = Cart.builder()
                ._id("cart123")
                .user(testUser)
                .items(new ArrayList<>())
                .totalPrice(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        cartService = cartServiceImpl;
    }

    @Test
    @DisplayName("TCS-001: Update cart item should update quantity when item exists")
    void TCS001_updateCartItem_shouldUpdateQuantity_whenItemExists() {
        // Arrange
        testCart.getItems().add(testCartItem);
        testCart.setTotalPrice(1800.0); // 2 * 900

        when(productRepository.findById("prod123")).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        cartService.updateCartItem("user123", "prod123", 3, "Black");

        // Assert
        verify(cartRepository).findByUserId("user123");
        verify(productRepository).findById("prod123");
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("TCS-002: Remove cart item should return updated user when item exists")
    void TCS002_removeCartItem_shouldReturnUpdatedUser_whenItemExists() {
        // Arrange
        testCart.getItems().add(testCartItem);
        testCart.setTotalPrice(1800.0); // 2 * 900

        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        User result = cartService.removeCartItem("user123", "prod123", "Black");

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(cartRepository).findByUserId("user123");
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("TCS-003: Update cart item should throw exception when product not found")
    void TCS003_updateCartItem_shouldThrowException_whenProductNotFound() {
        // Arrange
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(CustomException.class, () -> {
            cartService.updateCartItem("user123", "nonexistent", 1, "Black");
        }, "Should throw CustomException when product not found");
    }

    @Test
    @DisplayName("TCS-004: Update cart item should throw exception when insufficient stock")
    void TCS004_updateCartItem_shouldThrowException_whenInsufficientStock() {
        // Arrange
        when(productRepository.findById("prod123")).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(CustomException.class, () -> {
            cartService.updateCartItem("user123", "prod123", 20, "Black");
        }, "Should throw CustomException when insufficient stock");
    }
}
