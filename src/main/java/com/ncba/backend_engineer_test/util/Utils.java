package com.ncba.backend_engineer_test.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.util.ArrayList;

@Component
@Slf4j
@SuppressWarnings("java:S3740")
public class Utils {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String ALPHANUMERIC = ALPHABET + NUMBERS;
    private static final SecureRandom random = new SecureRandom();

    public  String generateIdentifier(int length) {
        StringBuilder sb = new StringBuilder(length);
        sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length()))); // Start with a letter
        for (int i = 1; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
    public String objectToString(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.info("ERROR CONVERTING OBJECT TO STRING:: {}", e.getMessage());
            return "";
        }
    }

}
