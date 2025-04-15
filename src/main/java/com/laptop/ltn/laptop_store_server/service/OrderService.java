package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.dto.request.MomoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderInfoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderProductStatusRequest;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface OrderService {
    MoMoResponse createOrder(OrderRequest request);
    String callBackPayment(MomoRequest request);
    MoMoResponse transactionStatus(String orderId);
    void deleteOrder(String orderId);

    // New methods
    Page<Order> getAllOrders(Map<String, String> params, Pageable pageable);
    Order updateOrderProductStatus(UpdateOrderProductStatusRequest request);
    Order updateOrderInfo(String orderId, UpdateOrderInfoRequest request);
}
