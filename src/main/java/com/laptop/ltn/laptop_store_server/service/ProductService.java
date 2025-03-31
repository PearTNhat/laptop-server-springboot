package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    MongoTemplate mongoTemplate;

    /**
     * Find a product by its ID
     */
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    /**
     * Find a product by its slug
     */
    public Optional<Product> findBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    /**
     * Get all products with filtering and pagination
     */
    public Map<String, Object> getAllProducts(int page, int size, String brand,
            Double minPrice, Double maxPrice,
            String sortBy, String order) {

        // Create query with filters
        Query query = new Query();

        if (brand != null && !brand.isEmpty()) {
            query.addCriteria(Criteria.where("brand").is(brand));
        }

        if (minPrice != null) {
            query.addCriteria(Criteria.where("discountPrice").gte(minPrice));
        }

        if (maxPrice != null) {
            query.addCriteria(Criteria.where("discountPrice").lte(maxPrice));
        }

        // Count total elements
        long total = mongoTemplate.count(query, Product.class);

        // Add pagination
        query.skip((long) page * size);
        query.limit(size);

        // Add sorting
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.with(Sort.by(direction, sortBy));

        // Execute query
        List<Product> products = mongoTemplate.find(query, Product.class);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("currentPage", page);
        response.put("totalItems", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));

        return response;
    }

    /**
     * Create a new product
     */
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    /**
     * Update an existing product
     */
    public Optional<Product> updateProduct(String id, Product productDetails) {
        return productRepository.findById(id).map(existingProduct -> {
            // Update the product fields with non-null values from productDetails
            if (productDetails.getTitle() != null)
                existingProduct.setTitle(productDetails.getTitle());
            if (productDetails.getSlug() != null)
                existingProduct.setSlug(productDetails.getSlug());
            if (productDetails.getDescription() != null)
                existingProduct.setDescription(productDetails.getDescription());
            if (productDetails.getFeatures() != null)
                existingProduct.setFeatures(productDetails.getFeatures());
            if (productDetails.getBrand() != null)
                existingProduct.setBrand(productDetails.getBrand());
            if (productDetails.getPrice() != null)
                existingProduct.setPrice(productDetails.getPrice());
            if (productDetails.getDiscountPrice() != null)
                existingProduct.setDiscountPrice(productDetails.getDiscountPrice());
            if (productDetails.getQuantity() != null)
                existingProduct.setQuantity(productDetails.getQuantity());
            if (productDetails.getSoldQuantity() != null)
                existingProduct.setSoldQuantity(productDetails.getSoldQuantity());
            if (productDetails.getPrimaryImage() != null)
                existingProduct.setPrimaryImage(productDetails.getPrimaryImage());
            if (productDetails.getColors() != null)
                existingProduct.setColors(productDetails.getColors());
            if (productDetails.getSeries() != null)
                existingProduct.setSeries(productDetails.getSeries());
            if (productDetails.getConfigs() != null)
                existingProduct.setConfigs(productDetails.getConfigs());
            if (productDetails.getTotalRating() != null)
                existingProduct.setTotalRating(productDetails.getTotalRating());

            existingProduct.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(existingProduct);
        });
    }

    /**
     * Delete a product by its ID
     */
    public boolean deleteProduct(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Search products by keyword
     */
    public Map<String, Object> searchProducts(String keyword, int page, int size) {
        Query query = new Query();

        // Search criteria
        if (keyword != null && !keyword.isEmpty()) {
            Criteria criteria = new Criteria().orOperator(
                    Criteria.where("title").regex(keyword, "i"),
                    Criteria.where("description").regex(keyword, "i"));
            query.addCriteria(criteria);
        }

        // Count total elements
        long total = mongoTemplate.count(query, Product.class);

        // Add pagination
        query.skip((long) page * size);
        query.limit(size);

        // Execute query
        List<Product> products = mongoTemplate.find(query, Product.class);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("currentPage", page);
        response.put("totalItems", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));

        return response;
    }
}
