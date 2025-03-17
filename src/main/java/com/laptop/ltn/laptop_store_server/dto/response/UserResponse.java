package com.laptop.ltn.laptop_store_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laptop.ltn.laptop_store_server.entity.CartItem;
import com.laptop.ltn.laptop_store_server.entity.Image;
import com.laptop.ltn.laptop_store_server.entity.WishListItem;
import lombok.*;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua giá trị null khi serialize
public class UserResponse {
    private String _id;

    private String firstName;
    private String lastName;

    private Image avatar;

    private String email;
    private String phone;
    private String address;

    private List<WishListItem> wishlist;

    private List<CartItem> carts;

    private String role ;
    private boolean isBlocked ;

    private Instant createdAt;
    private Instant updatedAt;
}
