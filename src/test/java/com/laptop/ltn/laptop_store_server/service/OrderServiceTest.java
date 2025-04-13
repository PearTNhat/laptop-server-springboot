package com.laptop.ltn.laptop_store_server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.MomoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderProductRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.entity.Order;
import com.laptop.ltn.laptop_store_server.entity.Payment;
import com.laptop.ltn.laptop_store_server.entity.Product;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.repository.OrderRepository;
import com.laptop.ltn.laptop_store_server.repository.PaymentRepository;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.impl.OrderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock WebClient webClient;
    @Mock WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock WebClient.RequestBodySpec requestBodySpec;
    @Mock WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock WebClient.ResponseSpec responseSpec;

    @Mock PaymentRepository paymentRepository;
    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock UserRepository userRepository;

    OrderImpl orderService;

    @BeforeEach
    void setup() {
        // Khởi tạo service với các giá trị cấu hình MoMo
        orderService = new OrderImpl(
                webClient,
                paymentRepository,
                orderRepository,
                productRepository,
                userRepository
        );
        // Gán giá trị @Value (có thể mock bằng reflection hoặc setter)
        ReflectionTestUtils.setField(orderService, "partnerCode", "MOMO");
        ReflectionTestUtils.setField(orderService, "accessKey", "accessKey");
        ReflectionTestUtils.setField(orderService, "secretKey", "secretKey");
        ReflectionTestUtils.setField(orderService, "redirectUrl", "http://localhost/redirect");
        ReflectionTestUtils.setField(orderService, "ipnUrl", "http://localhost/ipn");
        ReflectionTestUtils.setField(orderService, "requestType", "captureWallet");
        ReflectionTestUtils.setField(orderService, "endpoint", "https://test-payment.momo.vn/v2/gateway/api/create");
    }

    @Test
    void testCreateOrder_success() {
        // Fake dữ liệu
        OrderRequest orderRequest = OrderRequest.builder()
                .name("Test User")
                .phone("0123456789")
                .address("123 ABC")
                .total(100000L)
                .products(List.of(OrderProductRequest.builder()
                        .product(Product.builder()._id("1").build())
                        .quantity(2)
                        .build()))
                .build();

        MoMoResponse expectedResponse = MoMoResponse.builder()
                .payUrl("https://pay.momo.vn")
                .build();

        // Setup mock WebClient
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(MomoRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        when(responseSpec.bodyToMono(MoMoResponse.class)).thenReturn(Mono.justOrEmpty(null));
        when(responseSpec.bodyToMono(MoMoResponse.class)).thenReturn(Mono.just(expectedResponse));
        // Mock context security (nếu cần)
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Call service
        MoMoResponse actualResponse = orderService.createOrder(orderRequest);
        // Verify
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getPayUrl(), actualResponse.getPayUrl());
    }
    @Test
    void testTransactionStatus_success() throws JsonProcessingException {
        String orderId = "order123";
        // Set urlCheckTransaction để tránh lỗi uri(null)
        ReflectionTestUtils.setField(orderService, "urlCheckTransaction", "https://test-payment.momo.vn/v2/gateway/api/query");
        // Mock WebClient
        MoMoResponse momoResponse = MoMoResponse.builder()
                .resultCode(0)
                .orderId(orderId)
                .amount(100000L)
                .extraData(Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(
                        Map.of(
                                "address", "123 ABC",
                                "phone", "0123456789",
                                "name", "Test User",
                                "orderBy", "user123",
                                "total", 100000,
                                "products", List.of(Map.of(
                                        "quantity", 1,
                                        "color", "black",
                                        "product", Map.of("_id", "product123")
                                ))
                        )
                )))
                .build();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(MomoRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MoMoResponse.class)).thenReturn(Mono.just(momoResponse));

        // Chưa có payment trong DB
        when(paymentRepository.findByOrderId(orderId)).thenReturn(null);

        // Mock save payment
        Payment mockPayment = Payment.builder()._id("payment123").build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        // Mock product
        Product product = Product.builder()._id("product123").build();
        when(productRepository.findById("product123")).thenReturn(Optional.of(product));

        // Mock user
        User user = User.builder()._id("user123").build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        // Mock save order
        when(orderRepository.save(any(Order.class))).thenReturn(null);

        // Call method
        MoMoResponse result = orderService.transactionStatus(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testDeleteOrder_withNullOrderId_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.deleteOrder(null)
        );
        assertEquals("Missing input", exception.getMessage());
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteOrder_withBlankOrderId_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                orderService.deleteOrder(" ")
        );
        assertEquals("Missing input", exception.getMessage());
        verify(orderRepository, never()).deleteById(any());
    }

}

