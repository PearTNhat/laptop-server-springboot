package com.laptop.ltn.laptop_store_server.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    USER_EXISTED(1000, "User existed", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1002, "Username or password are incorrect", HttpStatus.UNAUTHORIZED),
    USERNAME_INVALID(1003, "Username must be at least 8 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "Username not existed", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1005, "You do not have permission", HttpStatus.FORBIDDEN),
    MISSING_TOKEN(1006, "Missing token or token invalid", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1008, "Invalid token", HttpStatus.UNAUTHORIZED),
    ID_NOT_EXIST(1009, "ID doest not exist", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(1010, "Role doest not exist", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1011, "Date of birth must be at least {min}", HttpStatus.BAD_REQUEST),
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
