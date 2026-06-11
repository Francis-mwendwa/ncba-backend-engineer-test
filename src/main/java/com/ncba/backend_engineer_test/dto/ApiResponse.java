package com.ncba.backend_engineer_test.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String statusCode;
    private String statusMessage;
    private T data;
}
