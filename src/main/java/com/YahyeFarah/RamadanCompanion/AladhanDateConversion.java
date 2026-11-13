
package com.YahyeFarah.RamadanCompanion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladhanDateConversion(Data data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(Gregorian gregorian, AladhanResponse.Hijri hijri) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Gregorian(String date) {}
}