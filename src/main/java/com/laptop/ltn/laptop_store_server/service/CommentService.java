package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Comment;
import java.util.Map;

public interface CommentService {
    Comment createComment(String userId, Map<String, Object> request);
    Comment updateComment(String commentId, String userId, String content, Integer rating);

    void deleteComment(String commentId, String userId);
    void likeComment(String commentId, String userId);
    void dislikeComment(String commentId, String userId);
}