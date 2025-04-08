package com.laptop.ltn.laptop_store_server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptop.ltn.laptop_store_server.dto.request.MomoRequest;
import com.laptop.ltn.laptop_store_server.dto.request.OrderRequest;
import com.laptop.ltn.laptop_store_server.dto.response.MoMoResponse;
import com.laptop.ltn.laptop_store_server.entity.*;
import com.laptop.ltn.laptop_store_server.repository.OrderRepository;
import com.laptop.ltn.laptop_store_server.repository.PaymentRepository;
import com.laptop.ltn.laptop_store_server.repository.ProductRepository;
import com.laptop.ltn.laptop_store_server.repository.UserRepository;
import com.laptop.ltn.laptop_store_server.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.utils.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderImpl implements OrderService {
    WebClient webClient=WebClient.create();
    PaymentRepository paymentRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    @Value(value = "${momo.partnerCode}")
    @NonFinal
    String partnerCode;
    @Value(value = "${momo.accessKey}")
    @NonFinal
    String accessKey;
    @Value(value = "${momo.secretKey}")
    @NonFinal
    String secretKey;
    @Value(value = "${momo.redirectUrl}")
    @NonFinal
    String redirectUrl;
    @Value(value = "${momo.ipnUrl}")
    @NonFinal
    String ipnUrl;
    @Value(value = "${momo.requestType}")
    @NonFinal
    String requestType;
    @Value(value = "${momo.endpoint}")
    @NonFinal
    String endpoint;

    @Override
    public MoMoResponse createOrder(OrderRequest request) {
        List<Product> products= request.getProducts();
        long total = request.getTotal();
        String address = request.getAddress();
        String phone = request.getPhone();
        String name = request.getName();
        String orderBy = SecurityContextHolder.getContext().getAuthentication().getName();
        String orderId = UUID.randomUUID().toString();
        String orderInfo = "Order information " + orderId;
        String requestId = UUID.randomUUID().toString();
        String extraData = createExtraData(products, total, address, phone, name, orderBy);
        String rawSignature = "accessKey=" + accessKey + "&amount=" + total + "&extraData=" + extraData + "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo + "&partnerCode=" + partnerCode + "&redirectUrl=" + redirectUrl + "&requestId=" + requestId + "&requestType=" + requestType;
        String prettySignature = "";

        try {
            prettySignature = generateHmacSHA256(rawSignature, secretKey);
        } catch (Exception e) {
            log.error("Error generating HMAC-SHA256 signature", e);
            throw new RuntimeException(e);
        }
        System.out.println(prettySignature);
        if (prettySignature.isBlank()) {
            log.error("Signature generation failed");
            throw new RuntimeException("Signature generation failed");
        }
        MomoRequest requestMomo = MomoRequest.builder()
                .partnerCode(partnerCode)
                .requestType(requestType)
                .redirectUrl(redirectUrl)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .amount(total)
                .extraData(extraData)
                .ipnUrl(ipnUrl)
                .signature(prettySignature)
                .lang("vi")
                .build();
        return webClient.post()
                .uri(endpoint)
                .header("Content-Type", "application/json; charset=UTF-8")
                .bodyValue(requestMomo)
                .retrieve()
                .bodyToMono(MoMoResponse.class)
                .block();
    }

    @Override
    public String callBackPayment(MomoRequest data) {
        try {
            if ("0".equals(data.getResultCode())) {
                // Giải mã extraData
                byte[] decodedBytes = Base64.getDecoder().decode(data.getExtraData());
                String decodedJson = new String(decodedBytes);
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> extraData = objectMapper.readValue(decodedJson, Map.class);

                // Tạo payment
                Payment payment = Payment.builder()
                        .orderId(data.getOrderId())
                        .payName("MoMo")
                        .total(data.getAmount())
                        .payType("qrMomo")
                        .build();
                Payment savedPayment = paymentRepository.save(payment);

                // Gọi phương thức tạo order (truyền extraData + paymentId)
                extraData.put("payInfo", savedPayment.get_id());
                List<Map<String, Object>> productMaps = (List<Map<String, Object>>) extraData.get("products");
                List<OrderProduct> orderProducts = productMaps.stream()
                        .map(item -> {
                            Map<String, Object> productMap = (Map<String, Object>) item.get("product");
                            String productId = (String) productMap.get("_id");

                            // Lấy sản phẩm từ DB bằng ID
                            Product product = productRepository.findById(productId)
                                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                            return OrderProduct.builder()
                                    .product(product)
                                    .quantity((Integer) item.get("quantity"))
                                    .color((String) item.get("color"))
                                    .status(0)
                                    .build();
                        })
                        .collect(Collectors.toList());
                User orderBy = userRepository.findById((String) extraData.get("orderBy"))
                        .orElseThrow(() -> new RuntimeException("User not found: " + extraData.get("orderBy")));
                Order order = Order.builder()
                        .products((List<OrderProduct>) orderProducts )
                        .total((Long) extraData.get("total"))
                        .address((String) extraData.get("address"))
                        .phone((String) extraData.get("phone"))
                        .name((String) extraData.get("name"))
                        .orderBy(orderBy)
                        .payInfo(savedPayment)
                        .build();
                orderRepository.save(order);
            }
            return "ok";
        } catch (Exception e) {
            log.error("Error processing callback payment", e);
            throw new RuntimeException("Error processing callback payment", e);
        }
    }


    public String generateHmacSHA256(String data, String key) throws Exception {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }
    public String createExtraData(Object products, double total, String address, String phone, String name, String orderBy) {
        // Tạo đối tượng dữ liệu cần mã hóa
        Map<String, Object> data = new HashMap<>();
        data.put("products", products);
        data.put("total", total);
        data.put("address", address);
        data.put("phone", phone);
        data.put("name", name);
        data.put("orderBy", orderBy);
        try {
            // Chuyển đối tượng thành chuỗi JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(data);

            // Mã hóa chuỗi JSON thành Base64
            return Base64.getEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        }
    }

}
