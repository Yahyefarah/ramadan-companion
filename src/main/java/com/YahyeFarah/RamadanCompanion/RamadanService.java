package com.YahyeFarah.RamadanCompanion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RamadanService {

    private static final int RAMADAN_MONTH = 9;
    private static final int SHAWWAL_MONTH = 10;
    private static final DateTimeFormatter ALADHAN_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final RestClient restClient;

    public RamadanService(RestClient aladhanRestClient) {
        this.restClient = aladhanRestClient;
    }

    public RamadanInfo getRamadanInfo(LocalDate today) {
        AladhanResponse.Hijri todayHijri = convert("/gToH/" + today.format(ALADHAN_DATE_FORMAT)).hijri();
        int hijriYear = Integer.parseInt(todayHijri.year());
        int hijriMonth = todayHijri.month().number();

        // Past Ramadan this Hijri year? Then the next one is next year.
        int ramadanYear = (hijriMonth > RAMADAN_MONTH) ? hijriYear + 1 : hijriYear;

        LocalDate start = toGregorian(1, RAMADAN_MONTH, ramadanYear);
        LocalDate eid = toGregorian(1, SHAWWAL_MONTH, ramadanYear);
        LocalDate end = eid.minusDays(1);

        boolean inProgress = !today.isBefore(start) && !today.isAfter(end);
        long daysUntilStart = inProgress ? 0 : ChronoUnit.DAYS.between(today, start);
        Integer ramadanDay = inProgress ? (int) ChronoUnit.DAYS.between(start, today) + 1 : null;
        int totalDays = (int) ChronoUnit.DAYS.between(start, eid);

        return new RamadanInfo(ramadanYear, inProgress, start, end, eid, daysUntilStart, ramadanDay, totalDays);
    }

    // Hijri day/month/year -> normal (Gregorian) date, e.g. 1 Ramadan 1448 -> 2027-02-08
    private LocalDate toGregorian(int day, int month, int year) {
        String hijri = String.format("%02d-%02d-%d", day, month, year);
        String gregorian = convert("/hToG/" + hijri).gregorian().date();
        return LocalDate.parse(gregorian, ALADHAN_DATE_FORMAT);
    }

    private AladhanDateConversion.Data convert(String path) {
        AladhanDateConversion response = restClient.get()
                .uri(path)
                .retrieve()
                .body(AladhanDateConversion.class);
        if (response == null || response.data() == null) {
            throw new IllegalStateException("No date conversion returned for " + path);
        }
        return response.data();
    }
}