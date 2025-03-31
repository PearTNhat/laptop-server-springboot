package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Cart;
import com.laptop.ltn.laptop_store_server.entity.CartItem;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.CustomException;
import com.laptop.ltn.laptop_store_server.repository.CartRepository;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.utils.TestDataDiffLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for CartServiceTest");

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

        logger.debug("Test data created: user={}, product={}, cart={}",
                testUser.getEmail(), testProduct.getTitle(), testCart.get_id());
    }

    @Test
    void getCartByUserId_shouldReturnCart_whenCartExists() {
        // Arrange
        logger.info("Testing getCartByUserId with user ID: user123");
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        // Act
        logger.debug("Executing cartService.getCartByUserId(user123)");
        Optional<Cart> result = cartService.getCartByUserId("user123");

        // Assert
        logger.info("Verifying getCartByUserId test results");
        assertTrue(result.isPresent(), "Cart should be present in result");
        assertEquals("cart123", result.get().get_id(), "Cart ID should match");
        verify(cartRepository).findByUserId("user123");
        logger.debug("getCartByUserId test completed successfully");
    }

    @Test
    void createCart_shouldCreateNewCart() {
        // Arrange
        logger.info("Testing createCart for user ID: user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        logger.debug("Executing cartService.createCart(user123)");
        Cart result = cartService.createCart("user123");

        // Assert
        logger.info("Verifying createCart test results");
        assertNotNull(result, "Cart should not be null");
        assertEquals("cart123", result.get_id(), "Cart ID should match");
        assertEquals(testUser, result.getUser(), "Cart user should match test user");
        verify(userRepository).findById("user123");
        verify(cartRepository).save(any(Cart.class));
        logger.debug("createCart test completed successfully");
    }

    @Test
    void getOrCreateCart_shouldReturnExistingCart_whenCartExists() {
        // Arrange
        logger.info("Testing getOrCreateCart when cart exists");
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        // Act
        logger.debug("Executing cartService.getOrCreateCart(user123)");
        Cart result = cartService.getOrCreateCart("user123");

        // Assert
        logger.info("Verifying getOrCreateCart test results");
        assertEquals("cart123", result.get_id(), "Cart ID should match");
        verify(cartRepository).findByUserId("user123");
        verify(userRepository, never()).findById(anyString());
        verify(cartRepository, never()).save(any(Cart.class));
        logger.debug("getOrCreateCart test completed successfully");
    }

    @Test
    void getOrCreateCart_shouldCreateNewCart_whenCartDoesNotExist() {
        // Arrange
        logger.info("Testing getOrCreateCart when cart doesn't exist");
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        logger.debug("Executing cartService.getOrCreateCart(user123)");
        Cart result = cartService.getOrCreateCart("user123");

        // Assert
        logger.info("Verifying getOrCreateCart test results");
        assertEquals("cart123", result.get_id(), "Cart ID should match");
        verify(cartRepository).findByUserId("user123");
        verify(userRepository).findById("user123");
        verify(cartRepository).save(any(Cart.class));
        logger.debug("getOrCreateCart test completed successfully");
    }

    @Test
    void addItemToCart_shouldAddNewItem_whenItemNotInCart() {
        // Arrange
        logger.info("Testing addItemToCart with new item");
        when(productRepository.findById("prod123")).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        // Save original cart for comparison
        Cart originalCart = Cart.builder()
                ._id(testCart.get_id())
                .user(testCart.getUser())
                .items(new ArrayList<>())
                .totalPrice(testCart.getTotalPrice())
                .createdAt(testCart.getCreatedAt())
                .updatedAt(testCart.getUpdatedAt())
                .build();

        Cart updatedCart = Cart.builder()
                ._id("cart123")
                .user(testUser)
                .items(List.of(testCartItem))
                .totalPrice(1800.0) // 2 * 900
                .createdAt(testCart.getCreatedAt())
                .updatedAt(testCart.getUpdatedAt())
                .build();

        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);

        // Act
        logger.debug("Executing cartService.addItemToCart");
        Cart result = cartService.addItemToCart("user123", "prod123", 2, "Black");

        // Assert
        logger.info("Verifying addItemToCart test results");
        assertEquals(1, result.getItems().size(), "Cart should have 1 item");
        assertEquals(testProduct, result.getItems().get(0).getProduct(), "Item product should match");
        assertEquals(2, result.getItems().get(0).getQuantity(), "Item quantity should be 2");
        assertEquals("Black", result.getItems().get(0).getColor(), "Item color should be Black");
        assertEquals(1800.0, result.getTotalPrice(), "Total price should be calculated correctly");
        verify(cartRepository).save(any(Cart.class));

        // Log differences between original and updated cart
        TestDataDiffLogger.logDiff("addItemToCart", originalCart, result);

        logger.debug("addItemToCart test completed successfully");
    }

    @Test
    void updateCartItem_shouldUpdateQuantity_whenItemExists() {
        // Arrange
        logger.info("Testing updateCartItem for existing item");
        testCart.getItems().add(testCartItem);
        testCart.setTotalPrice(1800.0); // 2 * 900

        // Create a copy of the original cart for comparison
        Cart originalCart = Cart.builder()
                ._id(testCart.get_id())
                .user(testCart.getUser())
                .items(new ArrayList<>(testCart.getItems()))
                .totalPrice(testCart.getTotalPrice())
                .createdAt(testCart.getCreatedAt())
                .updatedAt(testCart.getUpdatedAt())
                .build();

        when(productRepository.findById("prod123")).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        Cart updatedCart = Cart.builder()
                ._id("cart123")
                .user(testUser)
                .items(new ArrayList<>())
                .totalPrice(2700.0) // 3 * 900
                .createdAt(testCart.getCreatedAt())
                .updatedAt(testCart.getUpdatedAt())
                .build();

        CartItem updatedItem = CartItem.builder()
                .product(testProduct)
                .quantity(3)
                .color("Black")
                .build();

        updatedCart.getItems().add(updatedItem);

        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);

        // Act
        logger.debug("Executing cartService.updateCartItem");
        Cart result = cartService.updateCartItem("user123", "prod123", 3, "Black");

        // Assert
        logger.info("Verifying updateCartItem test results");
        assertEquals(1, result.getItems().size(), "Cart should have 1 item");
        assertEquals(3, result.getItems().get(0).getQuantity(), "Item quantity should be updated to 3");
        assertEquals(2700.0, result.getTotalPrice(), "Total price should be updated");
        verify(cartRepository).save(any(Cart.class));

        // Log differences between original and updated cart
        TestDataDiffLogger.logDiff("updateCartItem", originalCart, result);

        logger.debug("updateCartItem test completed successfully");
    }

    @Test
    void removeCartItem_shouldRemoveItem_whenItemExists() {
        // Arrange
        logger.info("Testing removeCartItem for existing item");
        testCart.getItems().add(testCartItem);
        testCart.setTotalPrice(1800.0); // 2 * 900

        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        Cart updatedCart = Cart.builder()
                ._id("cart123")
                .user(testUser)
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .createdAt(testCart.getCreatedAt())
                .updatedAt(testCart.getUpdatedAt())
                .build();

        when(cartRepository.save(any(Cart.class))).thenReturn(updatedCart);

        // Act
        logger.debug("Executing cartService.removeCartItem");
        Cart result = cartService.removeCartItem("user123", "prod123", "Black");

        // Assert
        logger.info("Verifying removeCartItem test results");
        assertTrue(result.getItems().isEmpty(), "Cart items should be empty");
        assertEquals(0.0, result.getTotalPrice(), "Total price should be 0");
        verify(cartRepository).save(any(Cart.class));
        logger.debug("removeCartItem test completed successfully");
    }

    @Test
    void clearCart_shouldRemoveAllItems() {
        // Arrange
        logger.info("Testing clearCart");
        testCart.getItems().add(testCartItem);
        testCart.setTotalPrice(1800.0); // 2 * 900

        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        Cart clearedCart = Cart.builder()
                ._id("cart123")
                .user(testUser)
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .createdAt(testCart.getCreatedAt())
                .updatedAt(testCart.getUpdatedAt())
                .build();

        when(cartRepository.save(any(Cart.class))).thenReturn(clearedCart);

        // Act
        logger.debug("Executing cartService.clearCart");
        Cart result = cartService.clearCart("user123");

        // Assert
        logger.info("Verifying clearCart test results");
        assertTrue(result.getItems().isEmpty(), "Cart items should be empty");
        assertEquals(0.0, result.getTotalPrice(), "Total price should be reset to 0");
        verify(cartRepository).save(any(Cart.class));
        logger.debug("clearCart test completed successfully");
    }

    @Test
    void getCartDetails_shouldReturnCartWithSummary() {
        // Arrange
        logger.info("Testing getCartDetails");
        testCart.getItems().add(testCartItem);
        testCart.setTotalPrice(1800.0); // 2 * 900

        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(testCart));

        // Act
        logger.debug("Executing cartService.getCartDetails");
        Map<String, Object> result = cartService.getCartDetails("user123");

        // Assert
        logger.info("Verifying getCartDetails test results");
        assertNotNull(result, "Result should not be null");
        assertEquals(testCart, result.get("cart"), "Cart should match test cart");
        assertEquals(1, result.get("itemCount"), "Item count should be 1");
        assertEquals(2, result.get("totalItems"), "Total items should be 2");
        assertEquals(1800.0, result.get("totalPrice"), "Total price should be 1800.0");
        logger.debug("getCartDetails test completed successfully");
    }

    @Test
    void addItemToCart_shouldThrowException_whenProductNotFound() {
        // Arrange
        logger.info("Testing addItemToCart with non-existent product");
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        logger.debug("Executing cartService.addItemToCart with invalid product ID");
        assertThrows(CustomException.class, () -> {
            cartService.addItemToCart("user123", "nonexistent", 1, "Black");
        }, "Should throw CustomException when product not found");
        logger.debug("addItemToCart exception test completed successfully");
    }

    @Test
    void addItemToCart_shouldThrowException_whenInsufficientStock() {
        // Arrange
        logger.info("Testing addItemToCart with insufficient stock");
        when(productRepository.findById("prod123")).thenReturn(Optional.of(testProduct));

        // Act & Assert
        logger.debug("Executing cartService.addItemToCart with quantity exceeding stock");
        assertThrows(CustomException.class, () -> {
            cartService.addItemToCart("user123", "prod123", 20, "Black");
        }, "Should throw CustomException when insufficient stock");
        logger.debug("addItemToCart insufficient stock test completed successfully");
    }
}
