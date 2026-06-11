package com.ncba.backend_engineer_test.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionStatus {

    FAILURE("1", "Failed"),
    SUCCESS("2", "Accepted");

    private String code;
    private String description;

}
