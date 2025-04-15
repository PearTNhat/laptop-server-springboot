package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;
    private MultipartFile testImage;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                ._id("1")
                .title("Test Laptop")
                .slug("test-laptop")
                .price(999)
                .discountPrice(899.0)
                .brand("Test Brand")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Create a mock image file for testing
        testImage = new MockMultipartFile(
                "primaryImage",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes());
    }

    @Test
    @DisplayName("TCC-004: Get product by slug when product exists should return product")
    void TCC004_getProductBySlug_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productService.findBySlug("test-laptop")).thenReturn(Optional.of(testProduct));

        // Act
        ResponseEntity<?> response = productController.getProductBySlug("test-laptop");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("success"));
        assertEquals(testProduct, body.get("data"));
        verify(productService).findBySlug("test-laptop");
    }

    @Test
    @DisplayName("TCC-005: Get product by slug when product does not exist should return not found")
    void TCC005_getProductBySlug_WhenProductDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(productService.findBySlug("non-existent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = productController.getProductBySlug("non-existent");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
        assertEquals("Product not found with slug: non-existent", body.get("message"));
        verify(productService).findBySlug("non-existent");
    }

    @Test
    @DisplayName("TCC-006: Create product should return created product")
    void TCC006_createProduct_ShouldReturnCreatedProduct() {
        // Arrange
        when(productService.createProduct(any(Product.class), any(MultipartFile.class))).thenReturn(testProduct);

        // Act
        ResponseEntity<?> response = productController.createProduct(testProduct, testImage);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testProduct, response.getBody());
        verify(productService).createProduct(any(Product.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("TCC-007: Update product when product exists should return updated product")
    void TCC007_updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() {
        // Arrange
        Product updatedProduct = Product.builder()
                .title("Updated Laptop")
                .build();
        when(productService.updateProduct("1", updatedProduct)).thenReturn(Optional.of(updatedProduct));

        // Act
        ResponseEntity<?> response = productController.updateProduct("1", updatedProduct);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProduct, response.getBody());
        verify(productService).updateProduct("1", updatedProduct);
    }

    @Test
    @DisplayName("TCC-008: Update product when product does not exist should return not found")
    void TCC008_updateProduct_WhenProductDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        Product updatedProduct = Product.builder().build();
        when(productService.updateProduct("2", updatedProduct)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = productController.updateProduct("2", updatedProduct);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product not found with id: 2", body.get("message"));
        verify(productService).updateProduct("2", updatedProduct);
    }

    @Test
    @DisplayName("TCC-009: Delete product when product exists should return success message")
    void TCC009_deleteProduct_WhenProductExists_ShouldReturnSuccessMessage() {
        // Arrange
        when(productService.deleteProduct("1")).thenReturn(true);

        // Act
        ResponseEntity<?> response = productController.deleteProduct("1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product deleted successfully", body.get("message"));
        verify(productService).deleteProduct("1");
    }

    @Test
    @DisplayName("TCC-010: Delete product when product does not exist should return not found")
    void TCC010_deleteProduct_WhenProductDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(productService.deleteProduct("2")).thenReturn(false);

        // Act
        ResponseEntity<?> response = productController.deleteProduct("2");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product not found with id: 2", body.get("message"));
        verify(productService).deleteProduct("2");
    }
}
