package com.laptop.ltn.laptop_store_server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct {
    @DocumentReference(collection = "products")
    private Product product;

    private Integer quantity;
    private String color;

    // -1: Cancelled, 0: Pending, 1: Confirmed
    private Integer status = 0;
}