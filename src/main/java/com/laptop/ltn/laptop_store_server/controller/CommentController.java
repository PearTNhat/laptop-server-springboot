package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.entity.Comment;
import com.laptop.ltn.laptop_store_server.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ApiResponse<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        return ApiResponse.<Comment>builder()
                .data(commentService.createComment(commentRequest.getProduct(), commentRequest.getRating(), commentRequest.getContent()))
                .build();
    }

    @PutMapping("/{commentId}")
    public Comment updateComment(@PathVariable String commentId, @RequestBody CommentRequest commentRequest) {
        return commentService.updateComment(commentId, commentRequest.getRating(), commentRequest.getContent());
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
    }

    @PostMapping("/like/{commentId}")
    public Comment likeComment(@PathVariable String commentId) {
        return commentService.likeComment(commentId);
    }

    @PostMapping("/dislike/{commentId}")
    public Comment dislikeComment(@PathVariable String commentId) {
        return commentService.dislikeComment(commentId);
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CommentRequest {
    private String product;
    private Integer rating;
    private String content;
}