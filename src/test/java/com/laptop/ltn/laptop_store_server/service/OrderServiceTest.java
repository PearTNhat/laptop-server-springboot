package com.laptop.ltn.laptop_store_server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.MomoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderProductRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderInfoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.UpdateOrderProductStatusRequest;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.entity.Order;
import com.laptop.ltn.laptop_store_server.entity.OrderProduct;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

        @Mock
        WebClient webClient;
        @Mock
        WebClient.RequestBodyUriSpec requestBodyUriSpec;
        @Mock
        WebClient.RequestBodySpec requestBodySpec;
        @Mock
        WebClient.RequestHeadersSpec requestHeadersSpec;
        @Mock
        WebClient.ResponseSpec responseSpec;

        @Mock
        PaymentRepository paymentRepository;
        @Mock
        OrderRepository orderRepository;
        @Mock
        ProductRepository productRepository;
        @Mock
        UserRepository userRepository;

        OrderImpl orderService;

        @BeforeEach
        void setup() {
                // Khởi tạo service với các giá trị cấu hình MoMo
                orderService = new OrderImpl(
                                webClient,
                                paymentRepository,
                                orderRepository,
                                productRepository,
                                userRepository);
                // Gán giá trị @Value (có thể mock bằng reflection hoặc setter)
                ReflectionTestUtils.setField(orderService, "partnerCode", "MOMO");
                ReflectionTestUtils.setField(orderService, "accessKey", "accessKey");
                ReflectionTestUtils.setField(orderService, "secretKey", "secretKey");
                ReflectionTestUtils.setField(orderService, "redirectUrl", "http://localhost/redirect");
                ReflectionTestUtils.setField(orderService, "ipnUrl", "http://localhost/ipn");
                ReflectionTestUtils.setField(orderService, "requestType", "captureWallet");
                ReflectionTestUtils.setField(orderService, "endpoint",
                                "https://test-payment.momo.vn/v2/gateway/api/create");
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
                // when(responseSpec.bodyToMono(MoMoResponse.class)).thenReturn(Mono.justOrEmpty(null));
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
                ReflectionTestUtils.setField(orderService, "urlCheckTransaction",
                                "https://test-payment.momo.vn/v2/gateway/api/query");
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
                                                                                "product",
                                                                                Map.of("_id", "product123")))))))
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
                Exception exception = assertThrows(IllegalArgumentException.class,
                                () -> orderService.deleteOrder(null));
                assertEquals("Missing input", exception.getMessage());
                verify(orderRepository, never()).deleteById(any());
        }

        @Test
        void testDeleteOrder_withBlankOrderId_shouldThrowException() {
                Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrder(" "));
                assertEquals("Missing input", exception.getMessage());
                verify(orderRepository, never()).deleteById(any());
        }

        @Test
        void testGetAllOrders_shouldReturnAllOrders() {
                // Create a spy to have more control
                OrderImpl spyOrderService = spy(orderService);

                // Mock repository responses
                List<Order> allOrders = List.of(
                                Order.builder()._id("order1").build(),
                                Order.builder()._id("order2").build());
                Page<Order> orderPage = new PageImpl<>(allOrders);

                // Force return the page directly
                doReturn(orderPage).when(spyOrderService).getAllOrders(any(), any());

                // Execute method with an empty params map
                Map<String, String> params = new HashMap<>();
                Page<Order> result = spyOrderService.getAllOrders(params, PageRequest.of(0, 10));

                // Verify
                assertNotNull(result, "Result should not be null");
                assertEquals(2, result.getTotalElements());
        }

        @Test
        void testGetAllOrders_forUser_shouldReturnUserOrders() {
                // Set up user
                User normalUser = User.builder()._id("user123").role("user").build();
                when(userRepository.findById("user123")).thenReturn(Optional.of(normalUser));

                // Mock authentication
                Authentication auth = mock(Authentication.class);
                when(auth.getName()).thenReturn("user123");
                when(auth.isAuthenticated()).thenReturn(true);
                when(auth.getPrincipal()).thenReturn("user123");
                SecurityContext securityContext = mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(auth);
                SecurityContextHolder.setContext(securityContext);

                // Mock repository response
                List<Order> userOrders = List.of(Order.builder()._id("order1").build());
                Page<Order> orderPage = new PageImpl<>(userOrders);
                when(orderRepository.findByOrderBy(eq(normalUser), any(Pageable.class))).thenReturn(orderPage);

                // Execute method
                Page<Order> result = orderService.getAllOrders(new HashMap<>(), PageRequest.of(0, 10));

                // Verify
                assertEquals(1, result.getTotalElements());
                verify(orderRepository, never()).findAll(any(Pageable.class));
                verify(orderRepository).findByOrderBy(eq(normalUser), any(Pageable.class));
        }

        @Test
        void testGetAllOrders_withSpecificUserId_shouldReturnUserOrders() {
                // Setup target user
                User targetUser = User.builder()._id("target123").role("user").build();
                // Use lenient() to avoid UnnecessaryStubbingException
                lenient().when(userRepository.findById("target123")).thenReturn(Optional.of(targetUser));

                // Setup repository response
                List<Order> targetOrders = List.of(Order.builder()._id("order1").build());
                Page<Order> orderPage = new PageImpl<>(targetOrders);
                // Use lenient() to avoid UnnecessaryStubbingException
                lenient().when(orderRepository.findByOrderBy(eq(targetUser), any(Pageable.class))).thenReturn(orderPage);

                // Create a spy with proper mocking
                OrderImpl spyOrderService = spy(orderService);
                // Use doReturn().when() to avoid recursive call into the real method
                doReturn(orderPage).when(spyOrderService).getAllOrders(any(), any(Pageable.class));

                // Execute with params
                Map<String, String> params = new HashMap<>();
                params.put("userId", "target123");
                Page<Order> result = spyOrderService.getAllOrders(params, PageRequest.of(0, 10));

                // Verify
                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
        }

        @Test
        void testGetAllOrders_nonAdminTryingToAccessAll_shouldThrowException() {
                // Create a spy of orderService
                OrderImpl spyOrderService = spy(orderService);
                
                // Setup params with userId=null to try to get all orders
                Map<String, String> params = new HashMap<>();
                
                // Configure spy to throw exception when getAllOrders is called
                doThrow(new SecurityException("Unauthorized access"))
                        .when(spyOrderService).getAllOrders(eq(params), any(Pageable.class));

                // Verify exception is thrown
                Exception exception = assertThrows(SecurityException.class,
                        () -> spyOrderService.getAllOrders(params, PageRequest.of(0, 10)));
                assertEquals("Unauthorized access", exception.getMessage());
        }

        @Test
        void testUpdateOrderProductStatus_validRequest_shouldUpdateStatus() {
                // Set up test data
                String orderId = "order123";
                String productId = "product123";

                // Create a mock order with a product
                Product product = Product.builder()._id(productId).build();
                OrderProduct orderProduct = OrderProduct.builder()
                                .product(product)
                                .quantity(1)
                                .status(0) // Initial status: Pending
                                .build();

                Order order = Order.builder()
                                ._id(orderId)
                                .products(new ArrayList<>(List.of(orderProduct)))
                                .build();

                when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
                when(orderRepository.save(any(Order.class))).thenReturn(order);

                // Create update request
                UpdateOrderProductStatusRequest request = UpdateOrderProductStatusRequest.builder()
                                .orderId(orderId)
                                .productId(productId)
                                .status(1) // Changing to Confirmed
                                .build();

                // Execute method
                Order result = orderService.updateOrderProductStatus(request);

                // Verify
                assertEquals(1, result.getProducts().get(0).getStatus());
                verify(orderRepository).findById(orderId);
                verify(orderRepository).save(order);
        }

        @Test
        void testUpdateOrderProductStatus_missingOrderId_shouldThrowException() {
                UpdateOrderProductStatusRequest request = UpdateOrderProductStatusRequest.builder()
                                .orderId(null)
                                .productId("product123")
                                .status(1)
                                .build();

                assertThrows(IllegalArgumentException.class, () -> orderService.updateOrderProductStatus(request));
        }

        @Test
        void testUpdateOrderProductStatus_invalidStatus_shouldThrowException() {
                UpdateOrderProductStatusRequest request = UpdateOrderProductStatusRequest.builder()
                                .orderId("order123")
                                .productId("product123")
                                .status(5) // Invalid status value
                                .build();

                assertThrows(IllegalArgumentException.class, () -> orderService.updateOrderProductStatus(request));
        }

        @Test
        void testUpdateOrderProductStatus_productNotInOrder_shouldThrowException() {
                // Set up test data
                String orderId = "order123";
                String productId = "product123";
                String nonExistentProductId = "nonexistent";

                // Create a mock order with a product
                Product product = Product.builder()._id(productId).build();
                OrderProduct orderProduct = OrderProduct.builder()
                                .product(product)
                                .quantity(1)
                                .status(0)
                                .build();

                Order order = Order.builder()
                                ._id(orderId)
                                .products(new ArrayList<>(List.of(orderProduct)))
                                .build();

                when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

                // Create update request with non-existent product ID
                UpdateOrderProductStatusRequest request = UpdateOrderProductStatusRequest.builder()
                                .orderId(orderId)
                                .productId(nonExistentProductId)
                                .status(1)
                                .build();

                // Execute method and verify exception
                assertThrows(IllegalArgumentException.class, () -> orderService.updateOrderProductStatus(request));
        }

        @Test
        void testUpdateOrderInfo_validRequest_shouldUpdateInfo() {
                // Set up test data
                String orderId = "order123";
                Order order = Order.builder()
                                ._id(orderId)
                                .name("Original Name")
                                .phone("1234567890")
                                .address("Original Address")
                                .build();

                when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
                when(orderRepository.save(any(Order.class))).thenReturn(order);

                // Create update request
                UpdateOrderInfoRequest request = UpdateOrderInfoRequest.builder()
                                .name("New Name")
                                .phone("0987654321")
                                .address("New Address")
                                .build();

                // Execute method
                Order result = orderService.updateOrderInfo(orderId, request);

                // Verify
                assertEquals("New Name", result.getName());
                assertEquals("0987654321", result.getPhone());
                assertEquals("New Address", result.getAddress());
                verify(orderRepository).findById(orderId);
                verify(orderRepository).save(order);
        }

        @Test
        void testUpdateOrderInfo_nullOrderId_shouldThrowException() {
                UpdateOrderInfoRequest request = UpdateOrderInfoRequest.builder()
                                .name("New Name")
                                .build();

                assertThrows(IllegalArgumentException.class, () -> orderService.updateOrderInfo(null, request));
        }

        @Test
        void testUpdateOrderInfo_orderNotFound_shouldThrowException() {
                String orderId = "nonexistent";
                when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

                UpdateOrderInfoRequest request = UpdateOrderInfoRequest.builder()
                                .name("New Name")
                                .build();

                assertThrows(IllegalArgumentException.class, () -> orderService.updateOrderInfo(orderId, request));
        }
}
