package com.laptop.ltn.laptop_store_server.dto.request;

import com.laptop.ltn.laptop_store_server.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductRequest {
    private String _id;
    private Product product;
    private String color;
    private Integer quantity;

}
