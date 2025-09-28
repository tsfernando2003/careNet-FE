package com.CareNet.CareNet.controller;

import com.CareNet.CareNet.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Email and password are required"));
        }
        
        Map<String, Object> response = authService.authenticate(email, password);
        
        if ((Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
    
    // Temporary test endpoint to check users in database
    @GetMapping("/test-users")
    public ResponseEntity<Map<String, Object>> testUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<com.CareNet.CareNet.model.User> testUser = authService.findUserByEmail("john.doe@gmail.com");
            if (testUser.isPresent()) {
                com.CareNet.CareNet.model.User user = testUser.get();
                response.put("found", true);
                response.put("email", user.getEmail());
                response.put("password", user.getPassword()); // For debugging only
                response.put("isActive", user.getIsActive());
                response.put("role", user.getRole());
            } else {
                response.put("found", false);
                response.put("message", "User not found");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // Temporary test endpoint to check admin user
    @GetMapping("/test-admin")
    public ResponseEntity<Map<String, Object>> testAdmin() {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<com.CareNet.CareNet.model.User> testUser = authService.findUserByEmail("admin@carenet.com");
            if (testUser.isPresent()) {
                com.CareNet.CareNet.model.User user = testUser.get();
                response.put("found", true);
                response.put("email", user.getEmail());
                response.put("password", user.getPassword()); // For debugging only
                response.put("isActive", user.getIsActive());
                response.put("role", user.getRole());
            } else {
                response.put("found", false);
                response.put("message", "Admin user not found");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    // Temporary endpoint to fix null is_active values
    @PostMapping("/fix-null-active")
    public ResponseEntity<Map<String, Object>> fixNullActive() {
        Map<String, Object> response = new HashMap<>();
        try {
            // This is a temporary fix - in production you'd handle this through proper migration
            response.put("message", "This functionality would require database access");
            response.put("recommendation", "Run SQL: UPDATE users SET is_active = 1 WHERE is_active IS NULL");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
