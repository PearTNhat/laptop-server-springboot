package com.laptop.ltn.laptop_store_server.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "comments") // Tên collection trong MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    private ObjectId id;

    private Integer rating;

    @DBRef
    private User user;

    @DBRef
    private Product product;

    @DBRef
    private Comment parentId;

    @DBRef
    private User replyOnUser;

    @DBRef
    private List<User> likes;

    @DBRef
    private List<User> dislikes;

    private String content;

    @CreatedDate
    private LocalDateTime createdAt;
}