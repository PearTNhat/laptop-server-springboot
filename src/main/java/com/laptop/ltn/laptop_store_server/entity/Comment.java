package com.laptop.ltn.laptop_store_server.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

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
    private String _id;

    private Integer rating;

    @DocumentReference(collection = "users")
    private User user;

    @DocumentReference(collection = "product")
    private Product product;

    @DocumentReference(collection = "comments")
    private Comment parentId;

    @DocumentReference(collection = "users")
    private User replyOnUser;

    @DocumentReference(collection = "users")
    private List<User> likes;

    @DocumentReference(collection = "users")
    private List<User> dislikes;

    private String content;

    @CreatedDate
    private LocalDateTime createdAt;
}