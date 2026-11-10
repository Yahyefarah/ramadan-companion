package com.YahyeFarah.RamadanCompanion;

import java.net.http.HttpClient;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PrayerTimesService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.aladhan.com/v1")
            .requestFactory(new JdkClientHttpRequestFactory(
                    HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .build()))
            .build();

    public PrayerTimes getToday(String city, String country) {
        AladhanResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/timingsByCity")
                        .queryParam("city", city)
                        .queryParam("country", country)
                        .queryParam("method", 2)
                        .build())
                .retrieve()
                .body(AladhanResponse.class);

        if (response == null || response.data() == null) {
            throw new IllegalStateException("No prayer times returned for " + city + ", " + country);
        }

        AladhanResponse.Timings t = response.data().timings();
        AladhanResponse.DateInfo d = response.data().date();

        return new PrayerTimes(
                city, country,
                d.readable(), d.hijri().date(), d.hijri().month().en(),
                t.fajr(), t.sunrise(), t.dhuhr(), t.asr(), t.maghrib(), t.isha(),
                t.fajr(), t.maghrib());
    }
}