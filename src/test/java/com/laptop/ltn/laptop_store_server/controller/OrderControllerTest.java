package com.laptop.ltn.laptop_store_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.OrderProductRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderInfoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderProductStatusRequest;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.entity.Order;
import com.laptop.ltn.laptop_store_server.entity.OrderProduct;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

        private MockMvc mockMvc;

        @Mock
        private OrderService orderService;

        @InjectMocks
        private OrderController orderController;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        public void setup() {
                // Sử dụng ExceptionHandler để xử lý ngoại lệ
                mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        @Test
        public void testGetAllOrders_shouldReturnOrders() throws Exception {
                // Tạo dữ liệu mẫu
                Order order1 = Order.builder()
                                ._id("order1")
                                .name("Customer 1")
                                .phone("123456789")
                                .address("Address 1")
                                .total(1000L)
                                .build();

                Order order2 = Order.builder()
                                ._id("order2")
                                .name("Customer 2")
                                .phone("987654321")
                                .address("Address 2")
                                .total(2000L)
                                .build();

                List<Order> orders = List.of(order1, order2);
                Page<Order> orderPage = new PageImpl<>(orders);

                // Mock phản hồi của service
                when(orderService.getAllOrders(any(), any(Pageable.class))).thenReturn(orderPage);

                // Thực hiện request GET và kiểm tra
                mockMvc.perform(get("/order")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "createdAt")
                                .param("direction", "desc"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.totalItems").value(2))
                                .andExpect(jsonPath("$.totalPages").value(1))
                                .andExpect(jsonPath("$.currentPage").value(0));
        }

        // @Test
        // public void testGetAllOrders_withParams_shouldReturnFilteredOrders() throws
        // Exception {
        // // Tạo dữ liệu mẫu
        // Order order = Order.builder()
        // ._id("order1")
        // .name("Customer 1")
        // .total(1000L)
        // .build();

        // List<Order> orders = List.of(order);
        // Page<Order> orderPage = new PageImpl<>(orders);

        // // Mock phản hồi của service với filter cụ thể userId
        // Map<String, String> expectedParams = new HashMap<>();
        // expectedParams.put("userId", "user123");
        // when(orderService.getAllOrders(eq(expectedParams),
        // any(Pageable.class))).thenReturn(orderPage);

        // // Thực hiện request GET với tham số userId
        // mockMvc.perform(get("/order")
        // .param("userId", "user123")
        // .param("page", "0")
        // .param("size", "10"))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.success").value(true))
        // .andExpect(jsonPath("$.data").isArray())
        // .andExpect(jsonPath("$.data.length()").value(1))
        // .andExpect(jsonPath("$.totalItems").value(1));
        // }

        @Test
        public void testUpdateOrderProductStatus_shouldUpdateSuccessfully() throws Exception {
                // Tạo dữ liệu mẫu
                String orderId = "order123";
                String productId = "product123";

                // Tạo product và orderProduct
                Product product = Product.builder()._id(productId).title("Test Product").build();
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setProduct(product);
                orderProduct.setQuantity(1);
                orderProduct.setStatus(1); // Đã được cập nhật

                // Tạo order
                Order order = Order.builder()
                                ._id(orderId)
                                .products(new ArrayList<>(List.of(orderProduct)))
                                .build();

                // Tạo request cập nhật
                UpdateOrderProductStatusRequest request = UpdateOrderProductStatusRequest.builder()
                                .orderId(orderId)
                                .productId(productId)
                                .status(1) // Trạng thái mới
                                .build();

                // Mock phản hồi service
                when(orderService.updateOrderProductStatus(any(UpdateOrderProductStatusRequest.class)))
                                .thenReturn(order);

                // Thực hiện request PUT và kiểm tra
                mockMvc.perform(put("/order/product-status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data._id").value(orderId))
                                .andExpect(jsonPath("$.message").value("Order product status updated successfully"));
        }

        @Test
        public void testUpdateOrderInfo_shouldUpdateSuccessfully() throws Exception {
                // Tạo dữ liệu mẫu
                String orderId = "order123";

                // Tạo order
                Order order = Order.builder()
                                ._id(orderId)
                                .name("New Name")
                                .phone("0987654321")
                                .address("New Address")
                                .build();

                // Tạo request cập nhật
                UpdateOrderInfoRequest request = UpdateOrderInfoRequest.builder()
                                .name("New Name")
                                .phone("0987654321")
                                .address("New Address")
                                .build();

                // Mock phản hồi service
                when(orderService.updateOrderInfo(eq(orderId), any(UpdateOrderInfoRequest.class))).thenReturn(order);

                // Thực hiện request PUT và kiểm tra
                mockMvc.perform(put("/order/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data._id").value(orderId))
                                .andExpect(jsonPath("$.message").value("Order information updated successfully"));
        }

        @Test
        public void testUpdateOrderProductStatus_withInvalidRequest_shouldReturnBadRequest() throws Exception {
                // Tạo request thiếu orderId
                UpdateOrderProductStatusRequest request = UpdateOrderProductStatusRequest.builder()
                                .orderId(null)
                                .productId("product123")
                                .status(1)
                                .build();

                // Mock service để ném exception với dữ liệu không hợp lệ
                when(orderService.updateOrderProductStatus(any(UpdateOrderProductStatusRequest.class)))
                                .thenThrow(new IllegalArgumentException(
                                                "Order ID, Product ID, and Status are required"));

                // Thực hiện request PUT và mong đợi bad request
                mockMvc.perform(put("/order/product-status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message")
                                                .value("Order ID, Product ID, and Status are required"));
        }

        @Test
        public void testUpdateOrderInfo_withOrderNotFound_shouldReturnBadRequest() throws Exception {
                String nonExistentOrderId = "nonexistent";

                // Tạo request cập nhật
                UpdateOrderInfoRequest request = UpdateOrderInfoRequest.builder()
                                .name("New Name")
                                .build();

                // Mock service để ném exception với order không tồn tại
                when(orderService.updateOrderInfo(eq(nonExistentOrderId), any(UpdateOrderInfoRequest.class)))
                                .thenThrow(new IllegalArgumentException("Order not found"));

                // Thực hiện request PUT và mong đợi bad request
                mockMvc.perform(put("/order/" + nonExistentOrderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Order not found"));
        }
}
