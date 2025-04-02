package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Comment;
import com.laptop.ltn.laptop_store_server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public Comment createComment(@RequestBody CommentRequest commentRequest) {
        return commentService.createComment(commentRequest.getProductId(), commentRequest.getRating(), commentRequest.getContent());
    }

    @PutMapping("/{commentId}")
    public Comment updateComment(@PathVariable String commentId, @RequestBody CommentRequest commentRequest) {
        return commentService.updateComment(commentId, commentRequest.getRating(), commentRequest.getContent());
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
    }

    @PostMapping("/{commentId}/like")
    public Comment likeComment(@PathVariable String commentId) {
        return commentService.likeComment(commentId);
    }

    @PostMapping("/{commentId}/dislike")
    public Comment dislikeComment(@PathVariable String commentId) {
        return commentService.dislikeComment(commentId);
    }
}

class CommentRequest {
    private String productId;
    private Integer rating;
    private String content;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}