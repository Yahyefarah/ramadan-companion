package com.YahyeFarah.RamadanCompanion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RamadanCalculatorTest {

    // Ramadan 1448 (expected): first fast Feb 8, 2027 · Eid al-Fitr Mar 9, 2027
    private static final LocalDate START = LocalDate.of(2027, 2, 8);
    private static final LocalDate EID = LocalDate.of(2027, 3, 9);

    @Test
    @DisplayName("Before Ramadan in the Hijri year -> this year's Ramadan")
    void nextRamadanYear_beforeRamadan() {
        assertEquals(1448, RamadanCalculator.nextRamadanYear(6, 1448));
    }

    @Test
    @DisplayName("During Ramadan -> the current Ramadan")
    void nextRamadanYear_duringRamadan() {
        assertEquals(1447, RamadanCalculator.nextRamadanYear(9, 1447));
    }

    @Test
    @DisplayName("After Ramadan (Shawwal) -> next year's Ramadan")
    void nextRamadanYear_afterRamadan() {
        assertEquals(1448, RamadanCalculator.nextRamadanYear(10, 1447));
    }

    @Test
    @DisplayName("Nov 12, 2026 is 88 days before Ramadan 1448")
    void beforeRamadan_countsDaysUntilStart() {
        RamadanInfo info = RamadanCalculator.buildInfo(LocalDate.of(2026, 11, 12), 1448, START, EID);

        assertFalse(info.inProgress());
        assertEquals(88, info.daysUntilStart());
        assertNull(info.ramadanDay());
    }

    @Test
    @DisplayName("The day before Ramadan is 1 day away")
    void dayBeforeRamadan_isOneDayAway() {
        RamadanInfo info = RamadanCalculator.buildInfo(START.minusDays(1), 1448, START, EID);

        assertFalse(info.inProgress());
        assertEquals(1, info.daysUntilStart());
    }

    @Test
    @DisplayName("The first day of Ramadan is Day 1")
    void firstDay_isDayOne() {
        RamadanInfo info = RamadanCalculator.buildInfo(START, 1448, START, EID);

        assertTrue(info.inProgress());
        assertEquals(1, info.ramadanDay());
        assertEquals(0, info.daysUntilStart());
    }

    @Test
    @DisplayName("The day before Eid is the last day of a 29-day Ramadan")
    void lastDay_isDayBeforeEid() {
        RamadanInfo info = RamadanCalculator.buildInfo(LocalDate.of(2027, 3, 8), 1448, START, EID);

        assertTrue(info.inProgress());
        assertEquals(29, info.ramadanDay());
        assertEquals(29, info.totalDays());
        assertEquals(LocalDate.of(2027, 3, 8), info.endDate());
    }

    @Test
    @DisplayName("A 30-day Ramadan (1447): March 1, 2026 is Day 12")
    void thirtyDayRamadan() {
        RamadanInfo info = RamadanCalculator.buildInfo(
                LocalDate.of(2026, 3, 1), 1447, LocalDate.of(2026, 2, 18), LocalDate.of(2026, 3, 20));

        assertEquals(30, info.totalDays());
        assertEquals(12, info.ramadanDay());
    }
}
