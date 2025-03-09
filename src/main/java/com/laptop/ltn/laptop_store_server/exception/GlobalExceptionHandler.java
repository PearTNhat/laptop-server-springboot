package com.laptop.ltn.laptop_store_server.exception;

import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Objects;

// Đây là annotation trong Spring dùng để khai báo một global exception handler.
// Lớp này giúp xử lý ngoại lệ cho tất cả các controller trong ứng dụng.
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";
    // bắt lỗi không chưa định nghĩa phí bên dưới
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Void>> exceptionHandler(Exception e) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(e.getMessage());
        apiResponse.setSuccess(false);
        return ResponseEntity.badRequest().body(apiResponse);
    }
    // Annotation này đánh dấu phương thức runtimeExceptionHandler là phương thức xử lý ngoại lệ cho RuntimeException
    // (và các lớp con của nó).
    // Khi một ngoại lệ RuntimeException xảy ra ở bất kỳ controller nào, Spring sẽ tự động gọi phương thức này.
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<Void>> runtimeExceptionHandler(RuntimeException e) {
        log.error(e.getMessage(), e);
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(e.getMessage());
        apiResponse.setSuccess(false);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> noResourceFoundException(NoResourceFoundException e) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Path does not exist");
        apiResponse.setSuccess(false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Void>> appExceptionHandler(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setSuccess(false);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

//    @ExceptionHandler(value = AuthorizationDeniedException.class)
//    ResponseEntity<ApiResponse<Void>> authorizationDeniedException(AuthorizationDeniedException e) {
//        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
//        ApiResponse<Void> apiResponse = new ApiResponse<>();
//        apiResponse.setMessage(errorCode.getMessage());
//        apiResponse.setSuccess(false);
//        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
//    }
    //    Lỗi validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumKey = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            var constraintViolations =
                    e.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attributes = constraintViolations.getConstraintDescriptor().getAttributes();
        } catch (IllegalArgumentException ex) {
        }
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());
        apiResponse.setSuccess(false);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = attributes.get(MIN_ATTRIBUTE).toString();
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
