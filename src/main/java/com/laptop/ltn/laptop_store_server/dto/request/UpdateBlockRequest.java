package com.laptop.ltn.laptop_store_server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlockRequest {
    private Boolean isBlocked;
    private String userId;
}
