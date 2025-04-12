package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.request.MomoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping("/payment")
    public ResponseEntity<MoMoResponse>  createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }
    @PostMapping("/payment/callback")
    public ResponseEntity<String> callBackPayment(@RequestBody MomoRequest request) {
        return ResponseEntity.ok(orderService.callBackPayment(request));
    }
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<MoMoResponse> createOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.transactionStatus(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<Void> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ApiResponse.<Void>builder()
                .message("Delete order successfully")
                .build();
    }
}
