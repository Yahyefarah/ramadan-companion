package com.YahyeFarah.RamadanCompanion;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RamadanController {

    private final RamadanService service;

    public RamadanController(RamadanService service) {
        this.service = service;
    }

    @GetMapping("/ramadan")
    public RamadanInfo ramadan(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getRamadanInfo(date != null ? date : LocalDate.now());
    }
}