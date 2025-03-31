package com.laptop.ltn.laptop_store_server.repository;

import com.laptop.ltn.laptop_store_server.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    // Using a custom query to avoid property path issues with underscore fields
    @Query("{ 'user._id' : ?0 }")
    Optional<Cart> findByUserId(String userId);
}
