package com.YahyeFarah.RamadanCompanion;

import java.time.LocalDate;

public record RamadanInfo(
        int hijriYear,
        boolean inProgress,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate eidDate,
        long daysUntilStart,
        Integer ramadanDay,
        int totalDays) {}
