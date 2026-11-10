package com.YahyeFarah.RamadanCompanion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladhanResponse(Data data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(Timings timings, DateInfo date) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Timings(
            @JsonProperty("Fajr") String fajr,
            @JsonProperty("Sunrise") String sunrise,
            @JsonProperty("Dhuhr") String dhuhr,
            @JsonProperty("Asr") String asr,
            @JsonProperty("Maghrib") String maghrib,
            @JsonProperty("Isha") String isha) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DateInfo(String readable, Hijri hijri) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hijri(String date, HijriMonth month, String year) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HijriMonth(int number, String en) {}
}