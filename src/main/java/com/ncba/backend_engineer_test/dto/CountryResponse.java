package com.ncba.backend_engineer_test.dto;

import lombok.*;

@Data
public class CountryResponse {
    private String isoCode;
    private String name;
    private String capital;
    private String currency;
    private String continent;
    private String language;
}