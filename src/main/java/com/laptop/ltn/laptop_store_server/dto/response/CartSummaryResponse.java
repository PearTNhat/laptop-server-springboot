package com.laptop.ltn.laptop_store_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laptop.ltn.laptop_store_server.entity.Cart;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartSummaryResponse {
    private Cart cart;
    private int itemCount;
    private int totalItems;
    private double totalPrice;
}
