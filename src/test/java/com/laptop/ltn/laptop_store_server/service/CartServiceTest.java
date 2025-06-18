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

    private static final String TEST_USER_ID = "672ae48eec87ff8e5a3dea26";
    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                ._id(TEST_USER_ID)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password")
                .role("user")
                .carts(new ArrayList<>())
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
    void TCS_Cart_updateCartItem_shouldUpdateQuantity_whenItemExists() {
        // Arrange
        testUser.getCarts().add(testCartItem);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        cartService.updateCartItem(TEST_USER_ID, "prod123", 3, "Black");

        // Assert
        assertEquals(3, testUser.getCarts().get(0).getQuantity());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("TCS-002: Remove cart item should return updated user when item exists")
    void TCS_Cart_removeCartItem_shouldReturnUpdatedUser_whenItemExists() {
        // Arrange
        testUser.getCarts().add(testCartItem);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = cartService.removeCartItem(TEST_USER_ID, "prod123", "Black");

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        assertTrue(testUser.getCarts().isEmpty());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("TCS-003: Update cart item should throw exception when product not found (add new)")
    void TCS_Cart_updateCartItem_shouldThrowException_whenProductNotFound() {
        // Arrange
        // testUser.carts không chứa sản phẩm này
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> {
            cartService.updateCartItem(TEST_USER_ID, "nonexistent", 1, "Black");
        }, "Should throw CustomException when product not found");
    }

}
