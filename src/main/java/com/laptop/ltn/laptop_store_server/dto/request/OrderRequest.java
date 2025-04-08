package com.laptop.ltn.laptop_store_server.dto.request;

import com.laptop.ltn.laptop_store_server.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    List<Product> products;
    long total;
    String address;
    String phone;
    String name;
}
