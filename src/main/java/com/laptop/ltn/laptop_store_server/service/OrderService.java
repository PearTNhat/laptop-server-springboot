package com.laptop.ltn.laptop_store_server.service;
import com.laptop.ltn.laptop_store_server.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getOrdersByUserId(String userId);
}
