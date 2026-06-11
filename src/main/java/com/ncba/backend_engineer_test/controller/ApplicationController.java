package com.ncba.backend_engineer_test.controller;

import com.ncba.backend_engineer_test.dto.ApiResponse;
import com.ncba.backend_engineer_test.dto.CountryResponse;
import com.ncba.backend_engineer_test.enums.TransactionStatus;
import com.ncba.backend_engineer_test.service.CountryService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/")
@SuppressWarnings("all")
@Slf4j
public class ApplicationController {
    private final CountryService service;

    public ApplicationController(CountryService service) {
        this.service = service;
    }

    private <T> ApiResponse<T> success(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setStatusCode(TransactionStatus.SUCCESS.getCode());
        resp.setStatusMessage(TransactionStatus.SUCCESS.getDescription());
        resp.setData(data);
        return resp;
    }

    private <T> ApiResponse<T> failure() {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setStatusCode(TransactionStatus.FAILURE.getCode());
        resp.setStatusMessage(TransactionStatus.FAILURE.getDescription());
        resp.setData(null);
        return resp;
    }

    @GetMapping("countries")
    public ApiResponse<Page<CountryResponse>> getCountries(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String continent,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<CountryResponse> result = service.searchCountries(name, continent, currency, language, pageable);
            return success(result);
        } catch (Exception e) {
            log.error("Error in getCountries: {}", e.getMessage(), e);
            return failure();
        }
    }

    @GetMapping("countries/{isoCode}")
    public ApiResponse<CountryResponse> getCountryByIso(@PathVariable String isoCode) {
        try {
            CountryResponse res = service.getCountryByIso(isoCode);
            if (res == null) {
                return failure();
            }
            return success(res);
        } catch (Exception e) {
            log.error("Error in getCountryByIso: {}", e.getMessage(), e);
            return failure();
        }
    }

    @GetMapping("countries/by-currency/{currency}")
    public ApiResponse<Page<CountryResponse>> getCountriesByCurrency(
            @PathVariable String currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<CountryResponse> result = service.searchCountries(null, null, currency, null, pageable);
            return success(result);
        } catch (Exception e) {
            log.error("Error in getCountriesByCurrency: {}", e.getMessage(), e);
            return failure();
        }
    }
}
