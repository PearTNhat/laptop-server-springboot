package com.laptop.ltn.laptop_store_server.repository;

import com.laptop.ltn.laptop_store_server.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    @Query(value = "{ 'user._id': ?0, 'product._id': ?1, 'rating': { $ne: null } }", exists = true)
    Boolean existsByUser_IdAndProduct_IdAndRatingNotNull(String userId, String productId);
    void deleteByParentId(String commentId);
    @Query("{ 'product._id': ?0, 'rating': { $ne: null } }")
    List<Comment> findByProductIdAndRatingIsNotNull(String productId);
}