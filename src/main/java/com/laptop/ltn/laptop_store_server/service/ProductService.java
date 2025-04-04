package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Product;

import java.util.Map;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(String id);

    Optional<Product> findBySlug(String slug);

    Map<String, Object> getAllProducts(int page, int size, String brand, Double minPrice, Double maxPrice,
            String sortBy, String order);

    Product createProduct(Product product);

    Optional<Product> updateProduct(String id, Product product);

    boolean deleteProduct(String id);

    Map<String, Object> searchProducts(String keyword, int page, int size);
}
