package com.CareNet.CareNet.controller;

import com.CareNet.CareNet.model.Caregiver;
import com.CareNet.CareNet.model.Document;
import com.CareNet.CareNet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.CareNet.CareNet.service.AdminService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        // Simple admin authentication - replace with proper authentication
        if ("admin".equals(username) && "password".equals(password)) {
            return ResponseEntity.ok(Map.of("token", "admin-token", "role", "admin"));
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping("/caregivers")
    public List<Caregiver> getAllCaregivers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Map<String, String> filters) {
        return adminService.getAllCaregivers(search, status);
    }

    @PostMapping("/files/{fileId}/status")
    public void changeDocumentStatus(@PathVariable Long fileId, @RequestBody Map<String, String> statusData) {
        String status = statusData.get("status");
        adminService.changeDocumentStatus(fileId, status);
    }

    @PostMapping("/caregivers/{id}/verify")
    public ResponseEntity<?> verifyCaregiver(@PathVariable Long id) {
        try {
            adminService.verifyCaregiver(id);
            return ResponseEntity.ok(Map.of("message", "Caregiver approved successfully", "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to approve caregiver: " + e.getMessage()));
        }
    }

    @PostMapping("/caregivers/{id}/reject")
    public ResponseEntity<?> rejectCaregiver(@PathVariable Long id) {
        try {
            adminService.rejectCaregiver(id);
            return ResponseEntity.ok(Map.of("message", "Caregiver rejected successfully", "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to reject caregiver: " + e.getMessage()));
        }
    }

    @PostMapping("/caregivers/{id}/status")
    public ResponseEntity<?> updateCaregiverStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData) {
        try {
            String status = statusData.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            adminService.updateCaregiverStatus(id, status);
            return ResponseEntity.ok(Map.of("message", "Status updated successfully", "id", id, "status", status));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/caregivers/{id}")
    public ResponseEntity<?> deleteCaregiver(@PathVariable Long id) {
        try {
            boolean deleted = adminService.deleteCaregiver(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Caregiver application deleted successfully", "id", id));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "Caregiver not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete caregiver: " + e.getMessage()));
        }
    }

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try {
            System.out.println("Attempting to download document with ID: " + id);
            
            Document document = adminService.getDocumentById(id);
            if (document != null) {
                System.out.println("Document found: " + document.getFileName());
                System.out.println("Document data length: " + (document.getData() != null ? document.getData().length : "null"));
                System.out.println("Document mime type: " + document.getMimeType());
                
                if (document.getData() != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", document.getMimeType() != null ? document.getMimeType() : "application/octet-stream");
                    headers.add("Content-Disposition", "attachment; filename=\"" + (document.getFileName() != null ? document.getFileName() : "document_" + id) + "\"");
                    headers.add("Access-Control-Expose-Headers", "Content-Disposition");
                    
                    System.out.println("Returning document with headers: " + headers);
                    
                    return ResponseEntity.ok()
                        .headers(headers)
                        .body(document.getData());
                } else {
                    System.err.println("Document data is null for ID: " + id);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }
            } else {
                System.err.println("Document not found for ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error downloading document: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Legacy endpoint for backward compatibility
    @GetMapping("/download-document/{id}")
    public ResponseEntity<byte[]> downloadDocumentLegacy(@PathVariable Long id) {
        return downloadDocument(id);
    }
    
    // Create sample data endpoint for testing
    @PostMapping("/create-sample-data")
    public ResponseEntity<?> createSampleData() {
        try {
            // Create sample caregiver application
            Optional<User> userOpt = adminService.findUserByEmail("john.doe@gmail.com");
            if (userOpt.isPresent()) {
                Map<String, Object> result = adminService.createSampleCaregiverData(userOpt.get());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.ok(Map.of("message", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create sample data: " + e.getMessage()));
        }
    }
}
