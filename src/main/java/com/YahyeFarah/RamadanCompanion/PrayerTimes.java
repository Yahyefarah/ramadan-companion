package com.YahyeFarah.RamadanCompanion;

public record PrayerTimes(
        String city,
        String country,
        String date,
        String hijriDate,
        String hijriMonth,
        String fajr,
        String sunrise,
        String dhuhr,
        String asr,
        String maghrib,
        String isha,
        String suhoorEnds,
        String iftar) {}