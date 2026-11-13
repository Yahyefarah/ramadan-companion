package com.YahyeFarah.RamadanCompanion;

import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AladhanClientConfig {

    @Bean
    public RestClient aladhanRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.aladhan.com/v1")
                .requestFactory(new JdkClientHttpRequestFactory(
                        HttpClient.newBuilder()
                                .followRedirects(HttpClient.Redirect.NORMAL)
                                .build()))
                .build();
    }
}
