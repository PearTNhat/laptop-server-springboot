package com.laptop.ltn.laptop_store_server.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users") // Tên collection trong MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private ObjectId id;

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    private Image avatar;
    @NotNull
    @Indexed(unique = true)
    private String email;

    private String phone;
    private String address;

    private List<WishListItem> wishlist; // Wishlist chứa danh sách Product

    private List<CartItem> carts;

    @NotNull
    private String role = "user";

    private boolean isBlocked = false;
    @NotNull
    private String password;
    private String refreshToken;

    private String passwordChangeAt;
    private String passwordResetToken;
    private String passwordResetExpires;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
