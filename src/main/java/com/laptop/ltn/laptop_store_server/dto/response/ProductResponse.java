package com.laptop.ltn.laptop_store_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laptop.ltn.laptop_store_server.entity.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private String _id;
    private String title;
    private String slug;
    private List<String> description;
    private List<String> features;
    private String brand;
    private Integer price;
    private Double discountPrice;
    private Integer quantity;
    private Integer soldQuantity;
    private Image primaryImage;
    private List<ColorVariant> colors;
    private Series series;
    private Configs configs;
    private Double totalRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
