package com.ncba.backend_engineer_test.exception;

import com.ncba.backend_engineer_test.dto.ApiResponse;
import com.ncba.backend_engineer_test.enums.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private <T> ApiResponse<T> failure() {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setStatusCode(TransactionStatus.FAILURE.getCode());
        resp.setStatusMessage(TransactionStatus.FAILURE.getDescription());
        resp.setData(null);
        return resp;
    }

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Object> handleNotFound(NotFoundException ex) {
        log.warn("NotFoundException: {}", ex.getMessage());
        return failure();
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return failure();
    }
}
