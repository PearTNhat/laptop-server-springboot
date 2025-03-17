package com.laptop.ltn.laptop_store_server.dto.response;

import com.laptop.ltn.laptop_store_server.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String accessToken;
    UserResponse userData;
}
