package com.laptop.ltn.laptop_store_server.constant;

public final class MomoParameter {
    // Partner information
    public static final String PARTNER_CODE = "partnerCode";
    public static final String PARTNER_CLIENT_ID = "partnerClientId";
    public static final String CALLBACK_TOKEN = "callbackToken";
    public static final String ACCESS_KEY = "accessKey";

    // Transaction information
    public static final String REQUEST_ID = "requestId";
    public static final String AMOUNT = "amount";
    public static final String ORDER_ID = "orderId";
    public static final String ORDER_INFO = "orderInfo";
    public static final String TRANS_ID = "transId";
    public static final String TOKEN = "token";

    // Request/response fields
    public static final String REQUEST_TYPE = "requestType";
    public static final String RESULT_CODE = "resultCode";
    public static final String MESSAGE = "message";
    public static final String DESCRIPTION = "description";

    // URLs
    public static final String PAY_URL = "payUrl";
    public static final String REDIRECT_URL = "redirectUrl";
    public static final String IPN_URL = "ipnUrl";

    // Additional data
    public static final String EXTRA_DATA = "extraData";

    private void MonoParameter() {
        // Private constructor to prevent instantiation
    }
}
