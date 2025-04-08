package com.laptop.ltn.laptop_store_server.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoMoResponse {
    private String partnerCode;
    private String requestId;
    private String orderId;
    private long amount;
    private String message;
    private int resultCode;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
    private String extraData;
    //
    private Long transId;
    private String payType;
    private Long responseTime;
    private String paymentOption;
}