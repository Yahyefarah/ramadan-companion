package com.YahyeFarah.RamadanCompanion;

import java.net.http.HttpClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PrayerTimesService {

    private static final int RAMADAN_MONTH = 9;
    private static final String DATE_FORMAT = "\\d{2}-\\d{2}-\\d{4}";

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.aladhan.com/v1")
            .requestFactory(new JdkClientHttpRequestFactory(
                    HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .build()))
            .build();

    public PrayerTimes getTimes(String city, String country, String date) {
        String path;
        if (date == null || date.isBlank()) {
            path = "/timingsByCity";
        } else if (date.matches(DATE_FORMAT)) {
            path = "/timingsByCity/" + date;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Date must be in DD-MM-YYYY format, for example 01-03-2026");
        }

        AladhanResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
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
        AladhanResponse.Hijri h = d.hijri();

        boolean ramadan = h.month().number() == RAMADAN_MONTH;
        Integer ramadanDay = ramadan ? Integer.parseInt(h.day()) : null;

        return new PrayerTimes(
                city, country,
                d.readable(), h.date(), h.month().en(),
                ramadan, ramadanDay,
                t.fajr(), t.sunrise(), t.dhuhr(), t.asr(), t.maghrib(), t.isha(),
                t.fajr(), t.maghrib());
    }
}