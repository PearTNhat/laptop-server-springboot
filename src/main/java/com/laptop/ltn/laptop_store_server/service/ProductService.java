package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Product;

import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Optional<Product> findById(String id);

    Optional<Product> findBySlug(String slug);

    Map<String, Object> getAllProducts(int page, int size, String brand, Double minPrice, Double maxPrice,
            String sort);

    Product createProduct(Product product, MultipartFile primaryImage);

    Optional<Product> updateProduct(String id, Product product);

    boolean deleteProduct(String id);

    Map<String, Object> searchProducts(String keyword, int page, int size);
}
