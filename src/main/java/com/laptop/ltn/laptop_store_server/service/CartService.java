package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Cart;
import com.laptop.ltn.laptop_store_server.entity.User;

import java.util.Map;
import java.util.Optional;

public interface CartService {
    Optional<Cart> getCartByUserId(String userId);

    Cart createCart(String userId);

    Cart getOrCreateCart(String userId);

    Map<String, Object> getCartDetails(String userId);

    Cart addItemToCart(String userId, String productId, int quantity, String color);

    void updateCartItem(String userId, String productId, int quantity, String color);

    User removeCartItem(String userId, String productId, String color);

    Cart clearCart(String userId);
}
