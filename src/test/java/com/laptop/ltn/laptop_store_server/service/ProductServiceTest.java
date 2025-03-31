package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
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
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test product data");
        testProduct = Product.builder()
                ._id("123")
                .title("Test Laptop")
                .slug("test-laptop")
                .brand("Test Brand")
                .price(1000)
                .discountPrice(900.0)
                .build();
        logger.debug("Test product created: {}", testProduct);
    }

    @Test
    void findById_shouldReturnProduct_whenProductExists() {
        // Arrange
        logger.info("Testing findById with product ID: 123");
        when(productRepository.findById("123")).thenReturn(Optional.of(testProduct));

        // Act
        logger.debug("Executing productService.findById(123)");
        Optional<Product> result = productService.findById("123");

        // Assert
        logger.info("Verifying findById test results");
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals("Test Laptop", result.get().getTitle(), "Product title should match");
        verify(productRepository).findById("123");
        logger.debug("findById test completed successfully");
    }

    @Test
    void findBySlug_shouldReturnProduct_whenProductExists() {
        // Arrange
        logger.info("Testing findBySlug with slug: test-laptop");
        when(productRepository.findBySlug("test-laptop")).thenReturn(Optional.of(testProduct));

        // Act
        logger.debug("Executing productService.findBySlug(test-laptop)");
        Optional<Product> result = productService.findBySlug("test-laptop");

        // Assert
        logger.info("Verifying findBySlug test results");
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals("123", result.get().get_id(), "Product ID should match");
        verify(productRepository).findBySlug("test-laptop");
        logger.debug("findBySlug test completed successfully");
    }

    @Test
    void getAllProducts_shouldReturnProductsWithPagination() {
        // Arrange
        logger.info("Testing getAllProducts with pagination");
        when(mongoTemplate.count(any(Query.class), eq(Product.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Product.class))).thenReturn(List.of(testProduct));

        // Act
        logger.debug("Executing productService.getAllProducts with page=0, size=10");
        Map<String, Object> result = productService.getAllProducts(0, 10, null, null, null, "createdAt", "desc");

        // Assert
        logger.info("Verifying getAllProducts test results");
        assertEquals(1, ((List<Product>) result.get("products")).size(), "Should return 1 product");
        assertEquals(0, result.get("currentPage"), "Current page should be 0");
        assertEquals(1L, result.get("totalItems"), "Total items should be 1");
        assertEquals(1, result.get("totalPages"), "Total pages should be 1");
        logger.debug("getAllProducts test completed successfully");
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        // Arrange
        logger.info("Testing createProduct");
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        logger.debug("Executing productService.createProduct");
        Product result = productService.createProduct(testProduct);

        // Assert
        logger.info("Verifying createProduct test results");
        assertEquals("Test Laptop", result.getTitle(), "Product title should match");
        assertNotNull(result.getCreatedAt(), "CreatedAt should not be null");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt should not be null");
        verify(productRepository).save(testProduct);
        logger.debug("createProduct test completed successfully");
    }

    @Test
    void updateProduct_shouldUpdateProduct_whenProductExists() {
        // Arrange
        logger.info("Testing updateProduct with product ID: 123");
        Product updatedProduct = Product.builder()
                .title("Updated Laptop")
                .build();

        when(productRepository.findById("123")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        logger.debug("Executing productService.updateProduct");
        Optional<Product> result = productService.updateProduct("123", updatedProduct);

        // Assert
        logger.info("Verifying updateProduct test results");
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals("Updated Laptop", result.get().getTitle(), "Product title should match");
        // Original fields should remain unchanged
        assertEquals("test-laptop", result.get().getSlug(), "Slug should remain unchanged");
        assertEquals("Test Brand", result.get().getBrand(), "Brand should remain unchanged");
        logger.debug("updateProduct test completed successfully");
    }

    @Test
    void deleteProduct_shouldReturnTrue_whenProductExists() {
        // Arrange
        logger.info("Testing deleteProduct with product ID: 123");
        when(productRepository.existsById("123")).thenReturn(true);
        doNothing().when(productRepository).deleteById("123");

        // Act
        logger.debug("Executing productService.deleteProduct");
        boolean result = productService.deleteProduct("123");

        // Assert
        logger.info("Verifying deleteProduct test results");
        assertTrue(result, "Delete operation should return true");
        verify(productRepository).deleteById("123");
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
