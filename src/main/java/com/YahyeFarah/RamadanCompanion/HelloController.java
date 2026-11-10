package com.YahyeFarah.RamadanCompanion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "Nashville") String city) {
        return "Assalamu alaikum from Ramadan Companion! City: " + city;
    }
}