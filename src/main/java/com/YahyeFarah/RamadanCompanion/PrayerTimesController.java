package com.YahyeFarah.RamadanCompanion;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PrayerTimesController {

    private final PrayerTimesService service;

    public PrayerTimesController(PrayerTimesService service) {
        this.service = service;
    }

    @GetMapping("/prayer-times")
    public PrayerTimes prayerTimes(
            @RequestParam(defaultValue = "Nashville") String city,
            @RequestParam(defaultValue = "US") String country,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getTimes(city, country, date);
    }
}