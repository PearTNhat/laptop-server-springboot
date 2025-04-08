package com.laptop.ltn.laptop_store_server.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.OrderProductRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private ObjectMapper objectMapper = new ObjectMapper();
    @BeforeEach
    public void setUp() {
        // Setup code if needed
    }
    @Test
    void testCreateOrder_shouldReturnMoMoResponse() throws Exception {
        // Giả lập request gửi từ client
        OrderRequest orderRequest = OrderRequest.builder()
                .name("John")
                .address("123 Street")
                .phone("0123456789")
                .total(100000)
                .products(List.of(
                        OrderProductRequest.builder().product(Product.builder()._id("1").build()).quantity(1).color("Đen").build()
                ))
                .build();

        // Giả lập MoMoResponse từ service
        MoMoResponse mockResponse = MoMoResponse.builder()
                .payUrl("https://test-payment.momo.vn/pay")
                .requestId("req123")
                .orderId("order123")
                .message("Thành công")
                .build();

        // Khi gọi service thì trả về mock
        Mockito.when(orderService.createOrder(Mockito.any(OrderRequest.class)))
                .thenReturn(mockResponse);

        // Gửi request lên controller
        mockMvc.perform(MockMvcRequestBuilders.post("/order/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.payUrl").value("https://test-payment.momo.vn/pay"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Thành công"));
    }
    @Test
    void testTransactionStatus_shouldReturnMoMoResponse() throws Exception {
        // Giả lập dữ liệu trả về từ service
        MoMoResponse mockResponse = MoMoResponse.builder()
                .orderId("order123")
                .requestId("order123")
                .payUrl("https://pay.momo.vn/abc")
                .resultCode(0)
                .message("Thành công")
                .amount(100000L)
                .build();

        // Khi gọi service thì trả về mock
        Mockito.when(orderService.transactionStatus("order123")).thenReturn(mockResponse);

        // Gửi POST request với path variable
        mockMvc.perform(MockMvcRequestBuilders.post("/order/payment/order123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId").value("order123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Thành công"));
    }

    @Test
    void testTransactionStatus_missingOrderId_shouldReturnBadRequest() throws Exception {
        // Gửi request với orderId rỗng sẽ bị lỗi nếu bạn validate
        mockMvc.perform(MockMvcRequestBuilders.post("/order/payment/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound()); // Vì thiếu path param
    }

    @Test
    void testTransactionStatus_invalidOrder_shouldThrowException() throws Exception {
        Mockito.when(orderService.transactionStatus("invalidId"))
                .thenThrow(new IllegalArgumentException("Missing input"));

        mockMvc.perform(MockMvcRequestBuilders.post("/order/payment/invalidId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
