package com.ncba.backend_engineer_test.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionStatus {

    SUCCESS("0", "Success"),
    FAILURE("1", "Failed");

    private final String code;
    private final String description;

}
