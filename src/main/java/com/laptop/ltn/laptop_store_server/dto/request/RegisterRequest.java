package com.laptop.ltn.laptop_store_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "FirstName is required")
    private String firstName;
    @NotBlank(message = "LastName is required")
    private String lastName;
    @NotBlank(message = "Password is required")
    private String password;
}
