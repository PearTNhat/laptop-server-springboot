package com.laptop.ltn.laptop_store_server.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "carts")
public class Cart {
    @Id
    private String _id;

    @DocumentReference(lazy = true)
    private User user;

    private List<CartItem> items = new ArrayList<>();
    private double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
