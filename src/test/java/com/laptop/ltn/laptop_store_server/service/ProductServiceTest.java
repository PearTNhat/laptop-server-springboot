package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.service.impl.ProductServiceImpl;
import com.laptop.ltn.laptop_store_server.utils.TestDataDiffLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
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

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    // Using the interface reference for tests
    private ProductService productService;

    private Product testProduct;

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

        // Set the implementation to the interface reference
        productService = productServiceImpl;
    }

    @Test
    void findById_WhenProductExists_ShouldReturnProduct() {
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
    void findById_WhenProductDoesNotExist_ShouldReturnEmpty() {
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
    void findBySlug_WhenProductExists_ShouldReturnProduct() {
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
    void createProduct_ShouldSetTimestampsAndSave() {
        // Arrange
        logger.info("Testing createProduct");
        Product newProduct = Product.builder()
                .title("New Laptop")
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        logger.debug("Executing productService.createProduct");
        Product result = productService.createProduct(newProduct);

        // Assert
        logger.info("Verifying createProduct test results");
        assertNotNull(result.getCreatedAt(), "CreatedAt should not be null");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt should not be null");
        verify(productRepository).save(any(Product.class));
        logger.debug("createProduct test completed successfully");
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateAndReturnProduct() {
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
    void updateProduct_WhenProductDoesNotExist_ShouldReturnEmpty() {
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
    void deleteProduct_WhenProductExists_ShouldReturnTrue() {
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
    void deleteProduct_WhenProductDoesNotExist_ShouldReturnFalse() {
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
    void searchProducts_shouldReturnMatchingProducts() {
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
