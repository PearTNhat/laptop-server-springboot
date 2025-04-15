package com.laptop.ltn.laptop_store_server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "series") 
public class Series {
    @Id
    private String _id;
    private String title;
    
    // Image structure to match Node.js model
    private Image image;
    
    // Store brand as ObjectId directly to match Node.js model
    // Not using @DBRef because Mongoose uses simple refs
    private ObjectId brand;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Image {
        private String url;
        private String public_id;
    }
}
