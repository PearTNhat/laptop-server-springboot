package com.laptop.ltn.laptop_store_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laptop.ltn.laptop_store_server.entity.Cart;
import com.laptop.ltn.laptop_store_server.entity.CartItem;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDetailResponse {
    private String id;
    private String userId;
    private List<CartItem> items;
    private double totalPrice;
    private int totalItems;
    private int itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
