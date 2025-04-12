package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.dto.request.MomoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;

public interface OrderService {
    MoMoResponse createOrder(OrderRequest request);
    String callBackPayment(MomoRequest request);
    MoMoResponse transactionStatus(String orderId);
    void deleteOrder(String orderId);
}
