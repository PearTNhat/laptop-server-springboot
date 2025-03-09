package com.laptop.ltn.laptop_store_server.entity;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @DocumentReference(collection = "products")
    private Product product;
    private int quantity;
    private String color;
}
