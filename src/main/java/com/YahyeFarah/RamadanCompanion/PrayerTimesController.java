package com.YahyeFarah.RamadanCompanion;

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
            @RequestParam(defaultValue = "US") String country) {
        return service.getToday(city, country);
    }
}