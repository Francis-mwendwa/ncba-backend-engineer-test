package com.ncba.backend_engineer_test.service;


import com.ncba.backend_engineer_test.clients.HttpClient;
import com.ncba.backend_engineer_test.config.AppProperties;
import com.ncba.backend_engineer_test.dto.CountryResponse;
import com.ncba.backend_engineer_test.entities.Country;
import com.ncba.backend_engineer_test.repos.CountryRepository;
import com.ncba.backend_engineer_test.util.SoapManipulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository repository;
    private final HttpClient httpClient;
    private final AppProperties appProperties;

    // Fetch all countries from SOAP and persist them. Returns first CountryResponse for backward compatibility.
    public CountryResponse getAllCountries() {

        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <web:FullCountryInfoAllCountries/>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        String endpoint = appProperties.getSoapEndpoint();
        HashMap<String, String> clientResponse = httpClient.httpPost(request, endpoint, "");

        String voomaResponseBody = clientResponse.get("RESPONSE_BODY");
        String responseCode = clientResponse.get("RESPONSE_CODE");
        if ("200".equals(responseCode)) {
            SoapManipulator soapManipulator = new SoapManipulator(voomaResponseBody);
            List<CountryResponse> countryDetails = soapManipulator.getCustomerAccounts("m:tCountryInfo");
            log.info("Country Details: {}", countryDetails);

            // Map the received country details to Country entities and save to DB
            List<Country> countries = new ArrayList<>();
            for (CountryResponse c : countryDetails) {
                Country country = Country.builder()
                        .isoCode(c.getIsoCode())
                        .name(c.getName())
                        .capital(c.getCapital())
                        .currency(c.getCurrency())
                        .continent(c.getContinent())
                        .language(c.getLanguage())
                        .build();
                countries.add(country);
            }

            if (!countries.isEmpty()) {
                repository.saveAll(countries);
            }

            log.info("Saved {} countries to database", countries.size());
            // Return the first country as a simple non-null response for the endpoint
            return countryDetails.isEmpty() ? null : countryDetails.get(0);
        } else {
            log.info("Response code not 200: {}", responseCode);
        }
        return null;
    }

    private CountryResponse mapToResponse(Country c) {
        CountryResponse r = new CountryResponse();
        r.setIsoCode(c.getIsoCode());
        r.setName(c.getName());
        r.setCapital(c.getCapital());
        r.setCurrency(c.getCurrency());
        r.setContinent(c.getContinent());
        r.setLanguage(c.getLanguage());
        return r;
    }

    public Page<CountryResponse> searchCountries(
            String name,
            String continent,
            String currency,
            String language,
            Pageable pageable
    ) {
        // Ensure data is present locally; if empty, fetch and persist
        if (repository.count() == 0) {
            try { getAllCountries(); } catch (Exception e)
            { log.warn("Initial sync failed: {}", e.getMessage()); }
        }

        Specification<Country> spec = (root, query, cb) -> cb.conjunction();
        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (continent != null && !continent.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("continent")), continent.toLowerCase()));
        }
        if (currency != null && !currency.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("currency")), currency.toLowerCase()));
        }
        if (language != null && !language.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("language")), "%" + language.toLowerCase() + "%"));
        }

        Page<Country> page = repository.findAll(spec, pageable);
        return page.map(this::mapToResponse);
    }

    public CountryResponse getCountryByIso(String isoCode) {
        // Ensure data exists
        if (repository.count() == 0) { getAllCountries(); }
        Optional<Country> found = repository.findAll(
                (root, query, cb) -> cb.equal(cb.lower(root.get("isoCode")), isoCode.toLowerCase())
        ).stream().findFirst();
        return found.map(this::mapToResponse)
                .orElseThrow(() -> new com.ncba.backend_engineer_test.exception.NotFoundException("Country with ISO code '" + isoCode + "' not found"));
    }
}