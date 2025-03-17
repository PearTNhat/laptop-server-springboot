package com.laptop.ltn.laptop_store_server.repository;

import com.laptop.ltn.laptop_store_server.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional <User> findByEmail(String email);
    User findByEmailRegex(String emailRegex);

    Boolean deleteByEmail(String email);

}