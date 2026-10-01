package com.dfine.dfineoms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminAnalyticsController {

    @GetMapping("/analytics")
    public ResponseEntity<?> getCustomerAnalytics() {
        // Only accessible if the JWT filter and Spring Security validate "STORE_STAFF" authority
        return ResponseEntity.ok("Secure analytics data loaded successfully.");
    }
}