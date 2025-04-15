package com.laptop.ltn.laptop_store_server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // Import JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty; // Import JsonProperty
import jakarta.validation.constraints.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient; // Import Transient
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String _id;

    @NotNull
    private String title;

    @NotBlank(message = "Slug cannot be blank")
    @Size(min = 3, message = "Slug must be at least 3 characters long")
    @Field("slug") // Tên trường trong MongoDB
    @Indexed(unique = true) // Đảm bảo slug là duy nhất
    private String slug;
    private List<String> description;
    private List<String> features;

    private String brand;

    private Integer price;
    @Min(value = 0, message = "Discount price must be at least 0")
    private Double discountPrice; // Giá giảm

    @AssertTrue(message = "Discount price should be less than or equal to the original price.")
    public boolean isValidDiscount() {
        return discountPrice == null || price == null || discountPrice <= price;
    }

    private Integer quantity = 0;
    private Integer soldQuantity = 0;

    private Image primaryImage;
    private List<ColorVariant> colors;

    @DocumentReference(collection = "series")
    @JsonIgnore // Ignore this field during JSON deserialization
    private Series series; // Tham chiếu đến Series

    @Transient // Mark as transient so it's not persisted directly
    @JsonProperty("seriesId") // Map JSON field "seriesId" to this field
    private String seriesId; // Temporary field to hold seriesId from JSON

    private Configs configs;
    private Double totalRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {
        this.configs = new Configs(); // Tạo configs mặc định
    }
}
