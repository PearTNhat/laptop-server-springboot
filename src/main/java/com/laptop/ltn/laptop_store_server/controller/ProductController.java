package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    /**
     * Get a single product by ID
     * 
     * @param id The product ID
     * @return The product if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        try {
            Optional<Product> product = productService.findById(id);
            if (product.isPresent()) {
                return ResponseEntity.ok(product.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Product not found with id: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error retrieving product: " + e.getMessage()));
        }
    }

    /**
     * Get a single product by slug
     * 
     * @param slug The product slug
     * @return The product if found, or 404 if not found
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getProductBySlug(@PathVariable String slug) {
        try {
            Optional<Product> product = productService.findBySlug(slug);
            if (product.isPresent()) {
                return ResponseEntity.ok(product.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Product not found with slug: " + slug));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error retrieving product: " + e.getMessage()));
        }
    }

    /**
     * Get multiple products with optional filtering and pagination
     * 
     * @param page     Page number (default 0)
     * @param size     Page size (default 10)
     * @param brand    Filter by brand (optional)
     * @param minPrice Filter by minimum price (optional)
     * @param maxPrice Filter by maximum price (optional)
     * @param sortBy   Sort field (default "createdAt")
     * @param order    Sort order: "asc" or "desc" (default "desc")
     * @return List of products and metadata
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        try {
            Map<String, Object> response = productService.getAllProducts(page, size, brand, minPrice, maxPrice, sortBy,
                    order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error retrieving products: " + e.getMessage()));
        }
    }

    /**
     * Create a new product
     * 
     * @param product The product data to create
     * @return The created product
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating product: " + e.getMessage()));
        }
    }

    /**
     * Update an existing product
     * 
     * @param id      The product ID to update
     * @param product The updated product data
     * @return The updated product if found, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Product product) {
        try {
            Optional<Product> updatedProduct = productService.updateProduct(id, product);
            if (updatedProduct.isPresent()) {
                return ResponseEntity.ok(updatedProduct.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Product not found with id: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating product: " + e.getMessage()));
        }
    }

    /**
     * Delete a product
     * 
     * @param id The product ID to delete
     * @return Success message or error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Product not found with id: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting product: " + e.getMessage()));
        }
    }

    /**
     * Search products by keyword
     * 
     * @param keyword The search keyword
     * @param page    Page number (default 0)
     * @param size    Page size (default 10)
     * @return List of matching products with pagination
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> response = productService.searchProducts(keyword, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error searching products: " + e.getMessage()));
        }
    }
}
