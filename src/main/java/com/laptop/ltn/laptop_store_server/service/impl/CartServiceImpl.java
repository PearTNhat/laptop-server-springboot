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
    public void updateCartItem(String userId, String productId, int quantity, String color) {
        if (productId == null || color == null) {
            throw new IllegalArgumentException("Missing inputs");
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        List<CartItem> carts = user.getCarts();

        Optional<CartItem> existingItemOpt = carts.stream()
                .filter(item -> productId.equals(item.getProduct().get_id()) && color.equals(item.getColor()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            for (CartItem item : carts) {
                if ( productId.equals(item.getProduct().get_id()) && color.equals(item.getColor())) {
                    item.setQuantity(quantity); // update số lượng
                    System.out.println("udpate quantity: " + item.getQuantity());
                    break;
                }
            }
        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setProduct(productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
            newItem.setQuantity(quantity);
            newItem.setColor(color);
            carts.add(newItem);
        }
        user.setCarts(carts);
        userRepository.save(user);
    }

    /**
     * Remove item from cart
     */
    @Override
    public User removeCartItem(String userId, String productId, String color) {
        // Get user's cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTED));

        // Count initial items
        List<CartItem> carts = user.getCarts();
        int initialSize = carts.size();

        // Filter out the item to remove
        List<CartItem> updatedItems = carts.stream()
                .filter(item -> !(item.getProduct().get_id().equals(productId) &&
                        color.equals(item.getColor()))
                )
                .collect(Collectors.toList());

        // Update cart items
        user.setCarts(updatedItems);

        // Recalculate cart total

        // Save and return updated cart
        return userRepository.save(user);
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
