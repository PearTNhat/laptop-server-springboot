package com.laptop.ltn.laptop_store_server.repository;

import com.laptop.ltn.laptop_store_server.entity.Brand;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends MongoRepository<Brand, String> {
    Optional<Brand> findBySlug(String slug);
}
