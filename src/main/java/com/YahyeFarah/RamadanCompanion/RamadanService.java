package com.YahyeFarah.RamadanCompanion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RamadanService {

    private static final int SHAWWAL_MONTH = 10;
    private static final DateTimeFormatter ALADHAN_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final RestClient restClient;

    public RamadanService(RestClient aladhanRestClient) {
        this.restClient = aladhanRestClient;
    }

    public RamadanInfo getRamadanInfo(LocalDate today) {
        AladhanResponse.Hijri todayHijri = convert("/gToH/" + today.format(ALADHAN_DATE_FORMAT)).hijri();

        int ramadanYear = RamadanCalculator.nextRamadanYear(
                todayHijri.month().number(), Integer.parseInt(todayHijri.year()));

        LocalDate start = toGregorian(1, RamadanCalculator.RAMADAN_MONTH, ramadanYear);
        LocalDate eid = toGregorian(1, SHAWWAL_MONTH, ramadanYear);

        return RamadanCalculator.buildInfo(today, ramadanYear, start, eid);
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