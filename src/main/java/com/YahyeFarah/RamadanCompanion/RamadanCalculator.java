package com.YahyeFarah.RamadanCompanion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class RamadanCalculator {

    public static final int RAMADAN_MONTH = 9;

    private RamadanCalculator() {
    }

    // Which Hijri year's Ramadan is next (or happening right now)
    public static int nextRamadanYear(int hijriMonth, int hijriYear) {
        return (hijriMonth > RAMADAN_MONTH) ? hijriYear + 1 : hijriYear;
    }

    // Builds the Ramadan summary for "today", given Ramadan's first day and Eid
    public static RamadanInfo buildInfo(LocalDate today, int ramadanYear, LocalDate start, LocalDate eid) {
        LocalDate end = eid.minusDays(1);
        boolean inProgress = !today.isBefore(start) && !today.isAfter(end);
        long daysUntilStart = inProgress ? 0 : ChronoUnit.DAYS.between(today, start);
        Integer ramadanDay = inProgress ? (int) ChronoUnit.DAYS.between(start, today) + 1 : null;
        int totalDays = (int) ChronoUnit.DAYS.between(start, eid);
        return new RamadanInfo(ramadanYear, inProgress, start, end, eid, daysUntilStart, ramadanDay, totalDays);
    }
}
