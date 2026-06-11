package com.ncba.backend_engineer_test.clients;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpClient {

    private final Utils utils;
    private WebClient webClient;
    private final WebClientConfig webClientConfig;
    private final ApplicationContext applicationContext;

    public ResponseEntity<String> httpPost(String requestBody,String endpoint,String messageId,
                                           String token , String operation) {
        try {

            String applicationName = applicationContext.getApplicationName();

            log.info(getLogMessageType(operation) + requestBody,applicationName,messageId, endpoint);

            this.webClient = webClientConfig.createWebClient();

            ResponseEntity<String> respEntity = webClient.post()
                    .uri(endpoint)//param
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            assert respEntity != null;
            if(respEntity.getBody() != null){
                log.info("CHANNEL RESPONSE "+ respEntity.getBody(),applicationName,messageId, endpoint);
            }
            return respEntity;

        } catch (Exception e) {
            log.error("An Exception occurred --- "  +e );
        }
        return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public String getLogMessageType (String operation) {
        String logMessage = "" ;

        switch (operation) {
            case "pushPayment": logMessage =  "OUTGOING PUSH REQUEST - > ";
            break;
            case "queryStatus": logMessage  ="OUTGOING PUSH QUERY STATUS REQUEST - > ";
            break;
            case "callback": logMessage =  "OUTGOING PUSH PAY CALLBACK REQUEST - > ";
            break;
            default:  logMessage =  "OUTGOING  REQUEST - > ";
        }

        return logMessage;
    }
}

