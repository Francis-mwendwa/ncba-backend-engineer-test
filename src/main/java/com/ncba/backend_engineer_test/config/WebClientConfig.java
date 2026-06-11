package com.ncba.backend_engineer_test.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
public class WebClientConfig {
    private final AppProperties appProperties;

    public WebClientConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public WebClient createWebClient() throws SSLException {

        ConnectionProvider connProvider = ConnectionProvider
                .builder("webclient-conn-pool")
                .maxConnections(appProperties.getMaxConnections())
                .maxIdleTime(Duration.ofSeconds(appProperties.getConnectionTimeoutInSeconds()))
                .maxLifeTime(Duration.ofSeconds(appProperties.getMaxLifeTimeInSeconds()))
                .pendingAcquireMaxCount(appProperties.getPendingAcquireMaxCount())
                .pendingAcquireTimeout(Duration.ofSeconds(appProperties.getPendingAcquireTimeoutInSeconds()))
                .build();

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient
                .create(connProvider)
                .secure(t -> t.sslContext(sslContext))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, appProperties.getConnectionTimeoutInSeconds()*1000)
                .responseTimeout(Duration.ofSeconds(appProperties.getConnectionTimeoutInSeconds()));

        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
}
