package com.laptop.ltn.laptop_store_server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Email is required")
    private String email;
    @NotNull(message ="Password is required")
    @NotBlank(message = "Password is required")
    private String password;
}
