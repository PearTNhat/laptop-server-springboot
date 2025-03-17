package com.laptop.ltn.laptop_store_server.repository;

import com.laptop.ltn.laptop_store_server.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
