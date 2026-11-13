package com.YahyeFarah.RamadanCompanion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PrayerTimesService {

    private static final int RAMADAN_MONTH = 9;
    private static final DateTimeFormatter ALADHAN_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final RestClient restClient;

    public PrayerTimesService(RestClient aladhanRestClient) {
        this.restClient = aladhanRestClient;
    }

    public PrayerTimes getTimes(String city, String country, LocalDate date) {
        String path = (date == null)
                ? "/timingsByCity"
                : "/timingsByCity/" + date.format(ALADHAN_DATE_FORMAT);

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