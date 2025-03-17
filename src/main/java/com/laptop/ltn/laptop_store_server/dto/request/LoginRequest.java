package com.laptop.ltn.laptop_store_server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message ="Password is required")
    @Min(value = 6, message = "Password must be latest 6 characters")
    private String password;
}
