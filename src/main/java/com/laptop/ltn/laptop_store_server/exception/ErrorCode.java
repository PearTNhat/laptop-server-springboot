package com.laptop.ltn.laptop_store_server.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    // not exist
    USER_NOT_EXISTED(1001, "Username not existed", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    ID_NOT_EXIST(1003, "ID doest not exist", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(1004, "Role doest not exist", HttpStatus.BAD_REQUEST),

    // invalid
    USERNAME_INVALID(2001, "Username must be at least 8 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(2002, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(2003, "Invalid token", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(2004, "Invalid key", HttpStatus.BAD_REQUEST),
    INVALID_DOB(2005, "Date of birth must be at least {min}", HttpStatus.BAD_REQUEST),

    // auth
    UNAUTHENTICATED(3001, "Username or password are incorrect", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(3002, "You do not have permission", HttpStatus.FORBIDDEN),
    MISSING_TOKEN(3003, "Missing token or token invalid", HttpStatus.UNAUTHORIZED),
    OTP_INCORRECT(3004, "OTP or Email incorrect", HttpStatus.BAD_REQUEST),
    BLOCKED(3005, "Your account has been blocked", HttpStatus.BAD_REQUEST),

    // Cart related errors
    CART_NOT_FOUND(4001, "Cart not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(4002, "Product not found", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(4003, "Insufficient product stock", HttpStatus.BAD_REQUEST),

    // mail
    SEND_MAIL_FAIL(1007,"Error while sending mail", HttpStatus.BAD_REQUEST)

    ;

    int code;
    String message;
    HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
