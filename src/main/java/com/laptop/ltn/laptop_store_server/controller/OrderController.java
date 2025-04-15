package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.request.MomoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderInfoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderProductStatusRequest;
import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.entity.Order;
import com.laptop.ltn.laptop_store_server.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping("/payment")
    public ApiResponse<MoMoResponse>  createOrder(@RequestBody OrderRequest request) {
        return ApiResponse.<MoMoResponse>builder()
                .data(orderService.createOrder(request))
                .build();
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

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        try {
            Sort sort = Sort.by(direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Order> orders = orderService.getAllOrders(params, pageable);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", orders.getContent(),
                    "totalItems", orders.getTotalElements(),
                    "totalPages", orders.getTotalPages(),
                    "currentPage", orders.getNumber()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error retrieving orders: " + e.getMessage()));
        }
    }

    @PutMapping("/product-status")
    public ResponseEntity<?> updateOrderProductStatus(@RequestBody UpdateOrderProductStatusRequest request) {
        try {
            Order updatedOrder = orderService.updateOrderProductStatus(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updatedOrder,
                    "message", "Order product status updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error updating order product status: " + e.getMessage()));
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrderInfo(
            @PathVariable String orderId,
            @RequestBody UpdateOrderInfoRequest request) {
        try {
            Order updatedOrder = orderService.updateOrderInfo(orderId, request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", updatedOrder,
                    "message", "Order information updated successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error updating order information: " + e.getMessage()
                    ));
        }
    }
}
