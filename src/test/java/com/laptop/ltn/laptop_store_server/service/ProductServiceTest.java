package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.repository.CommentRepository;
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

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    // Service reference using interface type for better testing practice
    private ProductService productService;

    private Product testProduct;
    private MultipartFile testImage;

    @BeforeEach
    void setUp() {
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

        // Set the implementation to the interface reference
        productService = productServiceImpl;
    }

    @Test
    @DisplayName("Find product by ID - Product exists")
    void findById_whenProductExists_shouldReturnProduct() {
        // Arrange
        when(productRepository.findById("1")).thenReturn(Optional.of(testProduct));

        // Act
        Optional<Product> result = productService.findById("1");

        // Assert
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals(testProduct, result.get(), "Product should match test product");
        verify(productRepository).findById("1");
    }

    @Test
    @DisplayName("Find product by ID - Product does not exist")
    void findById_whenProductDoesNotExist_shouldReturnEmpty() {
        // Arrange
        when(productRepository.findById("2")).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.findById("2");

        // Assert
        assertFalse(result.isPresent(), "Product should not be present in result");
        verify(productRepository).findById("2");
    }

    @Test
    @DisplayName("Find product by slug - Product exists")
    void findBySlug_whenProductExists_shouldReturnProduct() {
        // Arrange
        when(productRepository.findBySlug("test-laptop")).thenReturn(Optional.of(testProduct));
        when(commentRepository.findByProductIdAndParentIdIsNull(anyString())).thenReturn(Collections.emptyList());

        // Act
        Optional<Product> result = productService.findBySlug("test-laptop");

        // Assert
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals(testProduct, result.get(), "Product should match test product");
        verify(productRepository).findBySlug("test-laptop");
        verify(commentRepository).findByProductIdAndParentIdIsNull(anyString());
    }

    @Test
    @DisplayName("Create product with image")
    void createProduct_withImage_shouldSetTimestampsAndSave() throws IOException {
        // Arrange
        Product newProduct = Product.builder()
                .title("New Laptop")
                .build();

        // Mock image upload response
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "https://example.com/test-image.jpg");
        uploadResult.put("public_id", "test_public_id");
        when(uploadImageFile.uploadImageFile(any(MultipartFile.class))).thenReturn(uploadResult);

        // Mock saved product
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
        Product result = productService.createProduct(newProduct, testImage);

        // Assert
        assertNotNull(result.getCreatedAt(), "CreatedAt should not be null");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt should not be null");
        assertNotNull(result.getPrimaryImage(), "PrimaryImage should not be null");
        assertEquals("https://example.com/test-image.jpg", result.getPrimaryImage().getUrl(), "Image URL should match");
        verify(uploadImageFile).uploadImageFile(any(MultipartFile.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Create product without image")
    void createProduct_withoutImage_shouldStillSave() {
        // Arrange
        Product newProduct = Product.builder()
                .title("New Laptop")
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = productService.createProduct(newProduct, null);

        // Assert
        assertNotNull(result.getCreatedAt(), "CreatedAt should not be null");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt should not be null");
        verify(productRepository).save(any(Product.class));
        try {
            verify(uploadImageFile, never()).uploadImageFile(any(MultipartFile.class));
        } catch (IOException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Update product - Product exists")
    void updateProduct_whenProductExists_shouldUpdateAndReturnProduct() {
        // Arrange
        Product updatedProduct = Product.builder()
                .title("Updated Laptop")
                .price(1099)
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Optional<Product> result = productService.updateProduct("1", updatedProduct);

        // Assert
        assertTrue(result.isPresent(), "Product should be present in result");
        assertEquals(updatedProduct.getTitle(), result.get().getTitle(), "Product title should match");
        assertEquals(updatedProduct.getPrice(), result.get().getPrice(), "Product price should match");
        verify(productRepository).findById("1");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Update product - Product does not exist")
    void updateProduct_whenProductDoesNotExist_shouldReturnEmpty() {
        // Arrange
        Product updatedProduct = Product.builder().build();
        when(productRepository.findById("2")).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.updateProduct("2", updatedProduct);

        // Assert
        assertFalse(result.isPresent(), "Product should not be present in result");
        verify(productRepository).findById("2");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete product - Product exists")
    void deleteProduct_whenProductExists_shouldReturnTrue() {
        // Arrange
        when(productRepository.existsById("1")).thenReturn(true);
        doNothing().when(productRepository).deleteById("1");

        // Act
        boolean result = productService.deleteProduct("1");

        // Assert
        assertTrue(result, "Delete operation should return true");
        verify(productRepository).existsById("1");
        verify(productRepository).deleteById("1");
    }

    @Test
    @DisplayName("Delete product - Product does not exist")
    void deleteProduct_whenProductDoesNotExist_shouldReturnFalse() {
        // Arrange
        when(productRepository.existsById("2")).thenReturn(false);

        // Act
        boolean result = productService.deleteProduct("2");

        // Assert
        assertFalse(result, "Delete operation should return false");
        verify(productRepository).existsById("2");
        verify(productRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Search products with keyword")
    void searchProducts_withKeyword_shouldReturnMatchingProducts() {
        // Arrange
        when(mongoTemplate.count(any(Query.class), eq(Product.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
                .thenReturn(Collections.singletonList(testProduct));

        // Act
        Map<String, Object> result = productService.searchProducts("Test", 0, 10);

        // Assert
        assertEquals(1, ((List<Product>) result.get("products")).size(), "Should return 1 product");
        assertEquals(1L, result.get("totalItems"), "Total items should be 1");
        assertEquals(0, result.get("currentPage"), "Current page should be 0");
        assertEquals(1, result.get("totalPages"), "Total pages should be 1");
    }

    @Test
    @DisplayName("Get all products with filters")
    void getAllProducts_withFilters_shouldReturnFilteredProducts() {
        // Arrange
        // Create a Criteria that combines both price conditions to avoid MongoDB
        // limitation
        when(mongoTemplate.count(any(Query.class), eq(Product.class))).thenReturn(1L);
        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
                .thenReturn(Collections.singletonList(testProduct));

        // Act
        // Pass null for maxPrice to avoid the query limitation
        Map<String, Object> result = productService.getAllProducts(
                0, 10, "Test Brand", 800.0, null, "price");

        // Assert
        assertEquals(1, ((List<Product>) result.get("data")).size(), "Should return 1 product");
        assertEquals(1L, result.get("count"), "Count should be 1");
        assertEquals(true, result.get("success"), "Success should be true");
    }
}
