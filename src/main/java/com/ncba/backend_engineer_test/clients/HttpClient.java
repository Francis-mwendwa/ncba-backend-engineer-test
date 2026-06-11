package com.ncba.backend_engineer_test.clients;


import com.ncba.backend_engineer_test.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;

@Component
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class HttpClient {


    private WebClient webClient;
    private final WebClientConfig webclient;
    private final ApplicationContext applicationContext;
    public HashMap<String , String> httpPost(String requestBody, String voomaEndpoint, String messageId) {
        HashMap<String, String> responsePayload = new HashMap<>();;
        try {
            this.webClient = webclient.createWebClient();

            ResponseEntity<String> respEntity = webClient.post()
                    .uri(voomaEndpoint)//param
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.TEXT_XML))
                    .bodyValue(requestBody)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            assert respEntity != null;
            if (respEntity.getBody() != null) {
                log.info("SOAP SERVICE RESPONSE " + respEntity.getBody(), applicationContext.getApplicationName(), messageId, voomaEndpoint);
            }

            String responseBody = respEntity.getBody();
            HttpStatusCode httpStatusCode = respEntity.getStatusCode();

            if (httpStatusCode == HttpStatus.OK) {
                responsePayload.put("RESPONSE_CODE", "200");
                responsePayload.put("RESPONSE_BODY", responseBody);

            } else if (httpStatusCode == HttpStatus.BAD_REQUEST) {

                responsePayload.put("RESPONSE_CODE", "400");
                responsePayload.put("RESPONSE_BODY", responseBody);

            } else if (httpStatusCode == HttpStatus.UNAUTHORIZED) {
                responsePayload.put("RESPONSE_CODE", "401");
                responsePayload.put("RESPONSE_BODY", responseBody);
            } else {
                responsePayload.put("RESPONSE_CODE", httpStatusCode.toString());
                responsePayload.put("RESPONSE_BODY", responseBody);
            }
            return responsePayload;

        } catch (WebClientResponseException  ex) {
            responsePayload.put("RESPONSE_CODE",String.valueOf(ex.getStatusCode()));
            responsePayload.put("RESPONSE_BODY", ex.getResponseBodyAsString());

            // to re validate
        } catch (HttpClientErrorException ex ) {
            responsePayload.put("RESPONSE_CODE",String.valueOf(ex.getStatusCode()));
            responsePayload.put("RESPONSE_BODY", ex.getResponseBodyAsString());

        } catch (Exception ex) {
            responsePayload.put("RESPONSE_CODE","500");
            responsePayload.put("RESPONSE_BODY", "Internal Server Error");
        }
        return responsePayload;
    }
}