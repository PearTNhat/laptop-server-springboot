package com.laptop.ltn.laptop_store_server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class FinalRegisterRequest {
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    String email;
    @NotNull(message = "Otp is required")
    @NotBlank(message = "Otp is required")
    String otp;
}
