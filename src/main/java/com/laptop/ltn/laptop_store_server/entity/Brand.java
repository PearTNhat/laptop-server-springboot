package com.laptop.ltn.laptop_store_server.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "brands") // Tương đương với collection "Series" trong MongoDB
public class Brand {
    @Id
    private ObjectId id; // MongoDB sử dụng _id (String)

    private String title;

    private Image image;

    @NotNull
    @Indexed(unique = true)
    private String slug;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}