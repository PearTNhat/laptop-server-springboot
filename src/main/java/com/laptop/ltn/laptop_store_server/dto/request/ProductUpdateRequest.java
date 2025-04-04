package com.laptop.ltn.laptop_store_server.dto.request;

import com.laptop.ltn.laptop_store_server.entity.ColorVariant;
import com.laptop.ltn.laptop_store_server.entity.Configs;
import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.entity.Series;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    private String title;
    private String slug;
    private List<String> description;
    private List<String> features;
    private String brand;
    private Integer price;

    @Min(value = 0, message = "Discount price must be at least 0")
    private Double discountPrice;

    private Integer quantity;
    private Image primaryImage;
    private List<ColorVariant> colors;
    private Series series;
    private Configs configs;
}
