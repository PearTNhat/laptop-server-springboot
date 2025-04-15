package com.laptop.ltn.laptop_store_server.service.impl;

import com.laptop.ltn.laptop_store_server.entity.Comment;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.repository.CommentRepository;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Comment createComment(String userId, Map<String, Object> request) {
        String productId = (String) request.get("product");
        Integer rating = request.get("rating") != null ? Integer.valueOf(request.get("rating").toString()) : null;
        String parentId = (String) request.get("parentId");
        String content = (String) request.get("content");
        String replyOnUserId = (String) request.get("replyOnUser"); // Lấy ID dưới dạng String

        // Kiểm tra đầu vào
        if (productId == null) {
            throw new RuntimeException("Product Not Found");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Content cannot be empty");
        }

        if (rating != null && parentId != null) {
            throw new RuntimeException("Cannot provide both rating and parentId at the same time.");
        }

        if (rating == null && parentId == null) {
            throw new RuntimeException("Comment must have rating or parentId");
        }

        if (rating != null) {
            if (rating < 0 || rating > 5) {
                throw new RuntimeException("Rating must be between 0 and 5");
            }
            Boolean isRated = commentRepository.existsByUser_IdAndProduct_IdAndRatingNotNull(userId, productId);
            if (isRated != null && isRated) {
                throw new RuntimeException("You have already rated this product");
            }
            updateProductRating(productId, "CREATE", rating);
        }

        // Tìm kiếm các thực thể liên quan
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Comment parentComment = parentId != null ? commentRepository.findById(parentId).orElseThrow(() -> new RuntimeException("ParentId not found")) : null;

        // Xử lý replyOnUser
        User replyOnUser = null;
        if (replyOnUserId != null) {
            replyOnUser = userRepository.findById(replyOnUserId)
                    .orElseThrow(() -> new RuntimeException("User to reply not found"));
        }

        // Tạo và lưu comment
        Comment comment = Comment.builder()
                .user(user)
                .product(product)
                .content(content)
                .rating(rating)
                .parentId(parentComment)
                .replyOnUser(replyOnUser) // Truyền đối tượng User
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(String commentId, String userId, String content, Integer rating) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().get_id().equals(userId)) {
            throw new RuntimeException("You are not the owner of this comment");
        }

        if (content != null && !content.isEmpty()) {
            comment.setContent(content);
        }

        if (rating != null) {
            comment.setRating(rating);
            updateProductRating(comment.getProduct().get_id(), "UPDATE", rating);
        }

        // Lưu comment đã cập nhật
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(String commentId, String userId) {
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
//        if (!comment.getUser().get_id().equals(userId)) {
//            throw new RuntimeException("User not authorized");
//        }
//        if (comment.getRating() != null) {
//            updateProductRating(comment.getProduct().get_id(), "DELETE", -comment.getRating());
//        }
//        commentRepository.delete(comment);

        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

            if (!comment.getUser().get_id().equals(userId)) {
                throw new RuntimeException("Bạn không phải chủ sở hữu của bình luận này");
            }

            if (comment.getRating() != null) {
                updateProductRating(comment.getProduct().get_id(), "DELETE", -comment.getRating());
            }

            commentRepository.deleteById(commentId);
            commentRepository.deleteByParentId(commentId);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void likeComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (comment.getLikes().contains(user)) {
            comment.getLikes().remove(user);
        } else {
            comment.getLikes().add(user);
        }
        commentRepository.save(comment);
    }

    @Override
    public void dislikeComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (comment.getDislikes().contains(user)) {
            comment.getDislikes().remove(user);
        } else {
            comment.getDislikes().add(user);
        }
        commentRepository.save(comment);
    }

    public void updateProductRating(String productId, String type, double rating) {
        List<Comment> comments = commentRepository.findByProductIdAndRatingIsNotNull(productId);

        // Nếu không có comment nào, đặt totalRating = 0
        if (comments.isEmpty()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            product.setTotalRating(0.0);
            productRepository.save(product);
            return;
        }

        // Tính trung bình rating
        double sum = 0;
        for (Comment comment : comments) {
            sum += comment.getRating();
        }
        double averageRating = sum / comments.size();

        // Cập nhật totalRating của sản phẩm
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setTotalRating(averageRating);
        productRepository.save(product);
    }
}