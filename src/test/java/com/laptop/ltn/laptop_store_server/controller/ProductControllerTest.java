package com.laptop.ltn.laptop_store_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private Product testProduct;
    private MultipartFile testImage;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        // Initialize test data
        testProduct = Product.builder()
                ._id("1")
                .title("Test Laptop")
                .slug("test-laptop")
                .price(999)
                .discountPrice(899.0)
                .brand("Test Brand")
                .quantity(10)
                .soldQuantity(5)
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
    @DisplayName("TC-001: Get product by slug - Success")
    void getProductBySlug_whenProductExists_shouldReturnProduct() {
        // Arrange
        when(productService.findBySlug("test-laptop")).thenReturn(Optional.of(testProduct));

        // Act
        ResponseEntity<?> response = productController.getProductBySlug("test-laptop");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be 200 OK");
        assertTrue(response.getBody() instanceof Map, "Response body should be a Map");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("success"), "Success flag should be true");
        assertEquals(testProduct, body.get("data"), "Data should contain the test product");

        verify(productService).findBySlug("test-laptop");
    }

    @Test
    @DisplayName("TC-002: Get product by slug - Not Found")
    void getProductBySlug_whenProductDoesNotExist_shouldReturnNotFound() {
        // Arrange
        when(productService.findBySlug("non-existent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = productController.getProductBySlug("non-existent");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "HTTP status should be 404 Not Found");
        assertTrue(response.getBody() instanceof Map, "Response body should be a Map");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"), "Success flag should be false");
        assertEquals("Product not found with slug: non-existent", body.get("message"), "Message should indicate product not found");

        verify(productService).findBySlug("non-existent");
    }

    @Test
    @DisplayName("TC-003: Get all products - Success")
    void getAllProducts_shouldReturnProductsList() {
        // Arrange
        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("data", Collections.singletonList(testProduct));
        serviceResponse.put("count", 1L);
        serviceResponse.put("success", true);

        when(productService.getAllProducts(anyInt(), anyInt(), anyString(), any(), any(), anyString()))
                .thenReturn(serviceResponse);

        // Act
        ResponseEntity<?> response = productController.getAllProducts(0, 10, "Test Brand", 800.0, 1000.0, "-createdAt");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be 200 OK");
        assertEquals(serviceResponse, response.getBody(), "Response body should match service response");

        verify(productService).getAllProducts(0, 10, "Test Brand", 800.0, 1000.0, "-createdAt");
    }

    @Test
    @DisplayName("TC-004: Create product - Success")
    void createProduct_withValidData_shouldReturnCreatedProduct() throws Exception {
        // Arrange
        String documentJson = "{\"title\":\"New Laptop\",\"price\":999}";
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("title", "New Laptop");
        productMap.put("price", 999);

        // Fix the mock to use TypeReference instead of Class
        when(objectMapper.readValue(eq(documentJson), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(productMap);
        
        // Ensure we're properly capturing product creation
        when(productService.createProduct(any(Product.class), eq(testImage))).thenReturn(testProduct);

        // Act
        ResponseEntity<?> response = productController.createProduct(documentJson, testImage);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP status should be 201 Created");
        assertTrue(response.getBody() instanceof Map, "Response body should be a Map");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("success"), "Success flag should be true");
        assertEquals("Product created successfully", body.get("message"), "Message should indicate successful creation");
        assertEquals(testProduct, body.get("data"), "Data should contain the created product");

        verify(productService).createProduct(any(Product.class), eq(testImage));
    }

    @Test
    @DisplayName("TC-005: Update product - Success")
    void updateProduct_whenProductExists_shouldReturnUpdatedProduct() {
        // Arrange
        Product updatedProduct = Product.builder()
                .title("Updated Laptop")
                .price(1099)
                .build();

        when(productService.updateProduct("1", updatedProduct)).thenReturn(Optional.of(updatedProduct));

        // Act
        ResponseEntity<?> response = productController.updateProduct("1", updatedProduct);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be 200 OK");
        assertEquals(updatedProduct, response.getBody(), "Response body should contain the updated product");

        verify(productService).updateProduct("1", updatedProduct);
    }

    @Test
    @DisplayName("TC-006: Update product - Not Found")
    void updateProduct_whenProductDoesNotExist_shouldReturnNotFound() {
        // Arrange
        Product updatedProduct = Product.builder()
                .title("Updated Laptop")
                .build();

        when(productService.updateProduct("2", updatedProduct)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = productController.updateProduct("2", updatedProduct);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "HTTP status should be 404 Not Found");
        assertTrue(response.getBody() instanceof Map, "Response body should be a Map");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product not found with id: 2", body.get("message"), "Message should indicate product not found");

        verify(productService).updateProduct("2", updatedProduct);
    }

    @Test
    @DisplayName("TC-007: Delete product - Success")
    void deleteProduct_whenProductExists_shouldReturnSuccess() {
        // Arrange
        when(productService.deleteProduct("1")).thenReturn(true);

        // Act
        ResponseEntity<?> response = productController.deleteProduct("1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be 200 OK");
        assertTrue(response.getBody() instanceof Map, "Response body should be a Map");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product deleted successfully", body.get("message"), "Message should indicate successful deletion");

        verify(productService).deleteProduct("1");
    }

    @Test
    @DisplayName("TC-008: Delete product - Not Found")
    void deleteProduct_whenProductDoesNotExist_shouldReturnNotFound() {
        // Arrange
        when(productService.deleteProduct("2")).thenReturn(false);

        // Act
        ResponseEntity<?> response = productController.deleteProduct("2");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "HTTP status should be 404 Not Found");
        assertTrue(response.getBody() instanceof Map, "Response body should be a Map");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Product not found with id: 2", body.get("message"), "Message should indicate product not found");

        verify(productService).deleteProduct("2");
    }

    @Test
    @DisplayName("TC-009: Search products - Success")
    void searchProducts_shouldReturnMatchingProducts() {
        // Arrange
        Map<String, Object> searchResults = new HashMap<>();
        searchResults.put("products", Collections.singletonList(testProduct));
        searchResults.put("totalItems", 1L);
        searchResults.put("currentPage", 0);
        searchResults.put("totalPages", 1);

        when(productService.searchProducts("laptop", 0, 10)).thenReturn(searchResults);

        // Act
        ResponseEntity<?> response = productController.searchProducts("laptop", 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be 200 OK");
        assertEquals(searchResults, response.getBody(), "Response body should match search results");

        verify(productService).searchProducts("laptop", 0, 10);
    }

    @Test
    @DisplayName("TC-010: Handle exception in controller methods")
    void controllerMethods_whenExceptionOccurs_shouldReturnInternalServerError() {
        // Arrange
        when(productService.findBySlug("test-laptop")).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = productController.getProductBySlug("test-laptop");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "HTTP status should be 500 Internal Server Error");
        assertTrue(response.getBody() instanceof Map, "Response body should be a Map");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"), "Success flag should be false");
        assertTrue(((String) body.get("message")).contains("Error retrieving product"), "Message should indicate an error");

        verify(productService).findBySlug("test-laptop");
    }
}
