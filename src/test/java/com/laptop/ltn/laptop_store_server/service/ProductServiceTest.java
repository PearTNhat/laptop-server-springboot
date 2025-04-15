package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.service.impl.ProductServiceImpl;
import com.laptop.ltn.laptop_store_server.utils.TestDataDiffLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private UploadImageFile uploadImageFile;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    // Using the interface reference for tests
    private ProductService productService;

    private Product testProduct;
    private MultipartFile testImage;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test product data");
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

        // Set the implementation to the interface reference
        productService = productServiceImpl;
    }

    @Test
    @DisplayName("TCS-001: Find by ID when product exists should return product")
    void TCS001_findById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        logger.info("Testing findById with product ID: 1");
        when(productRepository.findById("1")).thenReturn(Optional.of(testProduct));

        // Act
        logger.debug("Executing productService.findById(1)");
        Optional<Product> result = productService.findById("1");

        // Assert
        logger.info("Verifying findById test results");
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals(testProduct, result.get(), "Product should match");
        verify(productRepository).findById("1");

        // Log any differences between the original and returned product
        TestDataDiffLogger.logDiff("findById", testProduct, result.orElse(null));

        logger.debug("findById test completed successfully");
    }

    @Test
    @DisplayName("TCS-002: Find by ID when product does not exist should return empty")
    void TCS002_findById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        logger.info("Testing findById with product ID: 2");
        when(productRepository.findById("2")).thenReturn(Optional.empty());

        // Act
        logger.debug("Executing productService.findById(2)");
        Optional<Product> result = productService.findById("2");

        // Assert
        logger.info("Verifying findById test results");
        assertFalse(result.isPresent(), "Product should not be present in result");
        verify(productRepository).findById("2");

        logger.debug("findById test completed successfully");
    }

    @Test
    @DisplayName("TCS-003: Find by slug when product exists should return product")
    void TCS003_findBySlug_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        logger.info("Testing findBySlug with slug: test-laptop");
        when(productRepository.findBySlug("test-laptop")).thenReturn(Optional.of(testProduct));

        // Act
        logger.debug("Executing productService.findBySlug(test-laptop)");
        Optional<Product> result = productService.findBySlug("test-laptop");

        // Assert
        logger.info("Verifying findBySlug test results");
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals(testProduct, result.get(), "Product should match");
        verify(productRepository).findBySlug("test-laptop");
        logger.debug("findBySlug test completed successfully");
    }

    @Test
    @DisplayName("TCS-004: Create product should set timestamps and save")
    void TCS004_createProduct_ShouldSetTimestampsAndSave() throws IOException {
        // Arrange
        logger.info("Testing createProduct");
        Product newProduct = Product.builder()
                .title("New Laptop")
                .build();

        // Mock the image upload response
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "https://example.com/test-image.jpg");
        uploadResult.put("public_id", "test_public_id");
        when(uploadImageFile.uploadImageFile(any(MultipartFile.class))).thenReturn(uploadResult);

        // Set up product to be returned after save
        Product savedProduct = Product.builder()
                .title("New Laptop")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .primaryImage(Image.builder()
                        .url("https://example.com/test-image.jpg")
                        .public_id("test_public_id")
                        .build())
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        logger.debug("Executing productService.createProduct with image");
        Product result = productService.createProduct(newProduct, testImage);

        // Assert
        logger.info("Verifying createProduct test results");
        assertNotNull(result.getCreatedAt(), "CreatedAt should not be null");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt should not be null");
        assertNotNull(result.getPrimaryImage(), "PrimaryImage should not be null");
        assertEquals("https://example.com/test-image.jpg", result.getPrimaryImage().getUrl(), "Image URL should match");
        verify(uploadImageFile).uploadImageFile(any(MultipartFile.class));
        verify(productRepository).save(any(Product.class));
        logger.debug("createProduct test completed successfully");
    }

    @Test
    @DisplayName("TCS-004.1: Create product without image should still save")
    void TCS004_1_createProduct_WithoutImage_ShouldStillSave() {
        // Arrange
        logger.info("Testing createProduct without image");
        Product newProduct = Product.builder()
                .title("New Laptop")
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        logger.debug("Executing productService.createProduct without image");
        Product result = productService.createProduct(newProduct, null);

        // Assert
        logger.info("Verifying createProduct test results");
        assertNotNull(result.getCreatedAt(), "CreatedAt should not be null");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt should not be null");
        verify(productRepository).save(any(Product.class));
        try {
            verify(uploadImageFile, never()).uploadImageFile(any(MultipartFile.class));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.debug("createProduct test completed successfully");
    }

    @Test
    @DisplayName("TCS-005: Update product when product exists should update and return product")
    void TCS005_updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
        // Arrange
        logger.info("Testing updateProduct with product ID: 1");
        Product updatedProduct = Product.builder()
                .title("Updated Laptop")
                .price(1099)
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        logger.debug("Executing productService.updateProduct");
        Optional<Product> result = productService.updateProduct("1", updatedProduct);

        // Assert
        logger.info("Verifying updateProduct test results");
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals(updatedProduct.getTitle(), result.get().getTitle(), "Product title should match");
        assertEquals(updatedProduct.getPrice(), result.get().getPrice(), "Product price should match");
        verify(productRepository).findById("1");
        verify(productRepository).save(any(Product.class));

        // Log differences between original and updated product
        TestDataDiffLogger.logDiff("updateProduct", testProduct, result.orElse(null));

        logger.debug("updateProduct test completed successfully");
    }

    @Test
    @DisplayName("TCS-006: Update product when product does not exist should return empty")
    void TCS006_updateProduct_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        logger.info("Testing updateProduct with product ID: 2");
        Product updatedProduct = Product.builder().build();
        when(productRepository.findById("2")).thenReturn(Optional.empty());

        // Act
        logger.debug("Executing productService.updateProduct");
        Optional<Product> result = productService.updateProduct("2", updatedProduct);

        // Assert
        logger.info("Verifying updateProduct test results");
        assertFalse(result.isPresent(), "Product should not be present in result");
        verify(productRepository).findById("2");
        verify(productRepository, never()).save(any(Product.class));

        logger.debug("updateProduct test completed successfully");
    }

    @Test
    @DisplayName("TCS-007: Delete product when product exists should return true")
    void TCS007_deleteProduct_WhenProductExists_ShouldReturnTrue() {
        // Arrange
        logger.info("Testing deleteProduct with product ID: 1");
        when(productRepository.existsById("1")).thenReturn(true);
        doNothing().when(productRepository).deleteById("1");

        // Act
        logger.debug("Executing productService.deleteProduct");
        boolean result = productService.deleteProduct("1");

        // Assert
        logger.info("Verifying deleteProduct test results");
        assertTrue(result, "Delete operation should return true");
        verify(productRepository).existsById("1");
        verify(productRepository).deleteById("1");
        logger.debug("deleteProduct test completed successfully");
    }

    @Test
    @DisplayName("TCS-008: Delete product when product does not exist should return false")
    void TCS008_deleteProduct_WhenProductDoesNotExist_ShouldReturnFalse() {
        // Arrange
        logger.info("Testing deleteProduct with product ID: 2");
        when(productRepository.existsById("2")).thenReturn(false);

        // Act
        logger.debug("Executing productService.deleteProduct");
        boolean result = productService.deleteProduct("2");

        // Assert
        logger.info("Verifying deleteProduct test results");
        assertFalse(result, "Delete operation should return false");
        verify(productRepository).existsById("2");
        verify(productRepository, never()).deleteById(anyString());
        logger.debug("deleteProduct test completed successfully");
    }

    @Test
    @DisplayName("TCS-009: Search products should return matching products")
    void TCS009_searchProducts_shouldReturnMatchingProducts() {
        // Arrange
        logger.info("Testing searchProducts with query: Test");
        when(mongoTemplate.count(any(Query.class), eq(Product.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
                .thenReturn(Collections.singletonList(testProduct));

        // Act
        logger.debug("Executing productService.searchProducts");
        Map<String, Object> result = productService.searchProducts("Test", 0, 10);

        // Assert
        logger.info("Verifying searchProducts test results");
        assertEquals(1, ((List<Product>) result.get("products")).size(), "Should return 1 product");
        assertEquals(1L, result.get("totalItems"), "Total items should be 1");
        logger.debug("searchProducts test completed successfully");
    }
}
