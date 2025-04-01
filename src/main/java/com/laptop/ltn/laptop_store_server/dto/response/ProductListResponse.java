package com.laptop.ltn.laptop_store_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductListResponse {
    private List<ProductResponse> products;
    private int currentPage;
    private long totalItems;
    private int totalPages;
}
