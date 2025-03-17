package com.laptop.ltn.laptop_store_server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "series") // Tương đương với collection "Series" trong MongoDB
public class Series {
    @Id
    private String _id; // MongoDB sử dụng _id (String)

    private String title;

    private Image image;

    @DocumentReference(collection = "brand") // Liên kết tới Brand (Tương đương ref: 'Brands' trong Mongoose)
    private Brand brand;


    @CreatedDate
    private Instant createdAt;  // Thời gian tạo

    @LastModifiedDate
    private Instant updatedAt;  // Thời gian cập nhật
}