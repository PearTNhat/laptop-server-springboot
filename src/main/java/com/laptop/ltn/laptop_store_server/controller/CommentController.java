package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Comment;
import com.laptop.ltn.laptop_store_server.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Comment comment = null;

        String userId = jwt.getSubject();
        comment = commentService.createComment(userId, request);
        response.put("success", true);
        response.put("data", comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String commentId,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        Comment updatedComment = null;

        String userId = jwt.getSubject();
        Integer rating = request.get("rating")== null ? null: Integer.parseInt(request.get("rating"));
        updatedComment = commentService.updateComment(commentId, userId, request.get("content"), rating);
        response.put("success", true);
        response.put("data", updatedComment);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String commentId) {
        Map<String, Object> response = new HashMap<>();

        String userId = jwt.getSubject();
        commentService.deleteComment(commentId, userId);
        response.put("success", true);
        response.put("message", "Comment deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/like/{commentId}")
    public ResponseEntity<Map<String, Object>> likeComment(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String commentId) {
        Map<String, Object> response = new HashMap<>();

        String userId = jwt.getSubject();
        commentService.likeComment(commentId, userId);
        response.put("success", true);
        response.put("message", "Comment liked successfully");
        return ResponseEntity.ok(response);
    }
}