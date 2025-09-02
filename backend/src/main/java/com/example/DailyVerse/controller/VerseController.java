package com.example.DailyVerse.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/verse")
public class VerseController {

    @PostMapping("/get")
    public Map<String, Object> getVerse(@RequestBody Map<String, String> body) {
        try {
            // Extract scripture from JSON body


            String scripture = body.get("scripture");

            System.err.println(scripture);

            // Build API URL
            String API_URL = "https://getcontext.xyz/api/api.php?query=" + scripture;

            // Call external API
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(API_URL, Map.class);

        } catch (Exception e) {
            return Map.of("error", "Failed to fetch verse", "message", e.getMessage());
        }
    }
}
