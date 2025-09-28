package com.CareNet.CareNet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> payload) {
        // This endpoint is a placeholder. The actual email sending will be handled by the frontend using EmailJS.
        return ResponseEntity.ok(Map.of("message", "Email notification queued successfully"));
    }
}
