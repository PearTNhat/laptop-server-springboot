package com.laptop.ltn.laptop_store_server.service.impl;

import com.laptop.ltn.laptop_store_server.entity.Cart;
import com.laptop.ltn.laptop_store_server.entity.CartItem;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.exception.CustomException;
import com.laptop.ltn.laptop_store_server.exception.ErrorCode;
import com.laptop.ltn.laptop_store_server.repository.CartRepository;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    /**
     * Get cart by user ID
     */
    @Override
    public Optional<Cart> getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    /**
     * Create a new cart for user
     */
    @Override
    public Cart createCart(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .totalPrice(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return cartRepository.save(cart);
    }

    /**
     * Get or create cart for user
     */
    @Override
    public Cart getOrCreateCart(String userId) {
        return getCartByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    /**
     * Get cart details with summary
     */
    @Override
    public Map<String, Object> getCartDetails(String userId) {
        // Get user's cart
        Cart cart = getOrCreateCart(userId);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("cart", cart);
        response.put("itemCount", cart.getItems().size());
        response.put("totalItems", cart.getItems().stream().mapToInt(CartItem::getQuantity).sum());
        response.put("totalPrice", cart.getTotalPrice());

        return response;
    }

    /**
     * Add item to cart
     */
    @Override
    public Cart addItemToCart(String userId, String productId, int quantity, String color) {
        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // Validate quantity
        if (product.getQuantity() < quantity) {
            throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
        }

        // Get user's cart
        Cart cart = getOrCreateCart(userId);

        // Find if product already exists in cart with same color
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().get_id().equals(productId) &&
                        (color == null || color.equals(item.getColor())))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing item
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .color(color)
                    .build();
            cart.getItems().add(newItem);
        }

        // Recalculate cart total
        updateCartTotal(cart);

        // Update timestamp
        cart.setUpdatedAt(LocalDateTime.now());

        // Save and return updated cart
        return cartRepository.save(cart);
    }

    /**
     * Update cart item quantity
     */
    @Override
    public Cart updateCartItem(String userId, String productId, int quantity, String color) {
        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // Get user's cart
        Cart cart = getCartByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        // Find item in cart
        Optional<CartItem> itemOptional = cart.getItems().stream()
                .filter(item -> item.getProduct().get_id().equals(productId) &&
                        (color == null || color.equals(item.getColor())))
                .findFirst();

        if (itemOptional.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found in cart");
        }

        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cart.getItems().remove(itemOptional.get());
        } else {
            // Validate stock
            if (product.getQuantity() < quantity) {
                throw new CustomException(ErrorCode.INSUFFICIENT_STOCK);
            }

            // Update quantity
            itemOptional.get().setQuantity(quantity);
        }

        // Recalculate cart total
        updateCartTotal(cart);

        // Update timestamp
        cart.setUpdatedAt(LocalDateTime.now());

        // Save and return updated cart
        return cartRepository.save(cart);
    }

    /**
     * Remove item from cart
     */
    @Override
    public Cart removeCartItem(String userId, String productId, String color) {
        // Get user's cart
        Cart cart = getCartByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        // Count initial items
        int initialSize = cart.getItems().size();

        // Filter out the item to remove
        List<CartItem> updatedItems = cart.getItems().stream()
                .filter(item -> !(item.getProduct().get_id().equals(productId) &&
                        (color == null || color.equals(item.getColor()))))
                .collect(Collectors.toList());

        // Check if item was removed
        if (updatedItems.size() == initialSize) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found in cart");
        }

        // Update cart items
        cart.setItems(updatedItems);

        // Recalculate cart total
        updateCartTotal(cart);

        // Update timestamp
        cart.setUpdatedAt(LocalDateTime.now());

        // Save and return updated cart
        return cartRepository.save(cart);
    }

    /**
     * Clear cart (remove all items)
     */
    @Override
    public Cart clearCart(String userId) {
        // Get user's cart
        Cart cart = getCartByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        // Clear all items
        cart.getItems().clear();
        cart.setTotalPrice(0);

        // Update timestamp
        cart.setUpdatedAt(LocalDateTime.now());

        // Save and return updated cart
        return cartRepository.save(cart);
    }

    /**
     * Helper method to recalculate cart total price
     */
    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> {
                    double itemPrice = item.getProduct().getDiscountPrice() != null
                            ? item.getProduct().getDiscountPrice()
                            : item.getProduct().getPrice();
                    return itemPrice * item.getQuantity();
                })
                .sum();
        cart.setTotalPrice(total);
    }
}
