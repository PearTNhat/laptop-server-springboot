package com.laptop.ltn.laptop_store_server.controller;

import com.fasterxml.jackson.core.type.TypeReference; // Import TypeReference
import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.Configs; // Import Configs if needed for manual mapping
import com.laptop.ltn.laptop_store_server.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections; // Import Collections
import java.util.List; // Import List
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;
    ObjectMapper objectMapper; // Inject ObjectMapper

    /**
     * Get a single product by slug
     * 
     * @param slug The product slug
     * @return The product if found, or 404 if not found
     */
    @GetMapping("/{slug}")
    public ResponseEntity<?> getProductBySlug(@PathVariable String slug) {
        try {
            Optional<Product> product = productService.findBySlug(slug);
            if (product.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", product.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Product not found with slug: " + slug));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error retrieving product: " + e.getMessage()));
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
     * @param sort     Sort field (default "createdAt"). Prefix with "-" for
     *                 descending, "+" for ascending.
     *                 Special values include:
     *                 "soldQuantity" - sort by sold quantity (best-selling)
     *                 "createdAt" - sort by creation date (new arrivals)
     *                 "price" - sort by discount price
     *                 "rating" - sort by total rating
     * @param order    Sort order: "asc" or "desc" (default "desc") - only used if
     *                 sort doesn't start with "-" or "+"
     * @return List of products and metadata
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "-createdAt") String sort) {
        try {
            Map<String, Object> response = productService.getAllProducts(page, size, brand, minPrice, maxPrice, sort);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error retrieving products: " + e.getMessage()));
        }
    }

    /**
     * Create a new product from multipart form data
     *
     * @param documentJson JSON string containing product data
     * @param primaryImage Optional primary image file
     * @return The created product or error response
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(
            @RequestPart("document") String documentJson,
            @RequestPart(value = "primaryImage", required = false) MultipartFile primaryImage) {
        try {
            // Deserialize the JSON string into a Map
            Map<String, Object> productMap = objectMapper.readValue(documentJson,
                    new TypeReference<Map<String, Object>>() {
                    });

            // Manually create and populate the Product object
            Product product = new Product();
            product.setTitle((String) productMap.get("title"));
            product.setBrand((String) productMap.get("brand")); // Assuming brand is sent as String name/slug
            product.setPrice((Integer) productMap.get("price"));
            // Handle potential NumberFormatException if discountPrice is not sent as Double
            Object discountPriceObj = productMap.get("discountPrice");
            if (discountPriceObj instanceof Number) {
                product.setDiscountPrice(((Number) discountPriceObj).doubleValue());
            } else if (discountPriceObj instanceof String) {
                try {
                    product.setDiscountPrice(Double.parseDouble((String) discountPriceObj));
                } catch (NumberFormatException e) {
                    // Handle error: invalid number format for discountPrice
                    throw new IllegalArgumentException("Invalid format for discountPrice: " + discountPriceObj);
                }
            }

            // Handle 'features' - convert String to List<String> if necessary
            Object featuresObj = productMap.get("features");
            if (featuresObj instanceof String) {
                product.setFeatures(Collections.singletonList((String) featuresObj));
            } else if (featuresObj instanceof List) {
                // If it's already a list (e.g., List<String>), cast and set
                try {
                    @SuppressWarnings("unchecked") // Suppress warning for unchecked cast
                    List<String> featuresList = (List<String>) featuresObj;
                    product.setFeatures(featuresList);
                } catch (ClassCastException e) {
                    // Handle error if the list contains non-String elements
                    throw new IllegalArgumentException("Invalid type for elements in 'features' list.");
                }
            }

            // Handle 'description' - convert String to List<String> if necessary
            Object descriptionObj = productMap.get("description");
            if (descriptionObj instanceof String) {
                product.setDescription(Collections.singletonList((String) descriptionObj));
            } else if (descriptionObj instanceof List) {
                try {
                    @SuppressWarnings("unchecked")
                    List<String> descriptionList = (List<String>) descriptionObj;
                    product.setDescription(descriptionList);
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Invalid type for elements in 'description' list.");
                }
            }

            // Map other fields similarly...
            product.setSeriesId((String) productMap.get("seriesId")); // Get seriesId for the service layer

            // Handle nested 'configs' object
            Object configsObj = productMap.get("configs");
            if (configsObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> configsMap = (Map<String, Object>) configsObj;
                // Use ObjectMapper to convert the map part to Configs object
                // Ensure Configs class has appropriate constructors/setters or is handled by
                // Jackson
                Configs configs = objectMapper.convertValue(configsMap, Configs.class);
                product.setConfigs(configs);
            } else {
                product.setConfigs(new Configs()); // Set default if not provided or invalid
            }

            // Call the service method with the manually constructed product and the image
            Product createdProduct = productService.createProduct(product, primaryImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Product created successfully",
                    "data", createdProduct));
        } catch (Exception e) {
            // Log the exception details for debugging
            // logger.error("Error creating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Error creating product: " + e.getMessage()));
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
