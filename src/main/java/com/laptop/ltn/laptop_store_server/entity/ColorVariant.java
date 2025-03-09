package com.laptop.ltn.laptop_store_server.entity;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorVariant {
    private String color;
    private Integer quantity = 0;
    private Integer soldQuantity = 0;
    private Image primaryImage;
    private List<Image> images;
}
