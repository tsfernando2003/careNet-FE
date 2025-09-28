package com.CareNet.CareNet.controller;
import com.CareNet.CareNet.model.Caregiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.lowagie.text.DocumentException;
import com.CareNet.CareNet.model.Caregiver;
import com.CareNet.CareNet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.CareNet.CareNet.service.CaregiverService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/caregivers")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class CaregiverController {

    @Autowired
    private CaregiverService caregiverService;

    // Submit new caregiver application (requires logged-in user)
    @PostMapping("/apply")
    public ResponseEntity<?> submitApplication(
            @RequestHeader("User-ID") Long userId,
            @RequestBody Map<String, Object> applicationData) {
        try {
            System.out.println("=== Submitting Caregiver Application ===");
            System.out.println("User ID: " + userId);
            
            // Parse application data
            Caregiver caregiverData = new Caregiver();
            caregiverData.setCaregiverType(Caregiver.CaregiverType.valueOf((String) applicationData.get("caregiverType")));
            caregiverData.setAddress((String) applicationData.get("address"));
            caregiverData.setCity((String) applicationData.get("city"));
            caregiverData.setState((String) applicationData.get("state"));
            caregiverData.setZipCode((String) applicationData.get("zipCode"));
            
            if (applicationData.get("dateOfBirth") != null) {
                LocalDate dateOfBirth = LocalDate.parse((String) applicationData.get("dateOfBirth"));
                // Validate that date of birth is in the past
                if (dateOfBirth.isAfter(LocalDate.now()) || dateOfBirth.equals(LocalDate.now())) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Date of birth must be a date in the past");
                    errorResponse.put("timestamp", new Date());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
                caregiverData.setDateOfBirth(dateOfBirth);
            }
            
            caregiverData.setSsn((String) applicationData.get("ssn"));
            caregiverData.setEmergencyContactName((String) applicationData.get("emergencyContactName"));
            caregiverData.setEmergencyContactPhone((String) applicationData.get("emergencyContactPhone"));
            caregiverData.setExperience((String) applicationData.get("experience"));
            caregiverData.setCertifications((String) applicationData.get("certifications"));
            caregiverData.setAvailability((String) applicationData.get("availability"));
            caregiverData.setSpecializations((String) applicationData.get("specializations"));
            caregiverData.setApplicationEmail((String) applicationData.get("email"));
            
            Caregiver savedApplication = caregiverService.createCaregiverApplication(userId, caregiverData, null);
            return ResponseEntity.ok(savedApplication);
        } catch (Exception e) {
            System.out.println("Error submitting application: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            
            // Check for database enum constraint error
            if (e.getMessage().contains("Data truncation") || 
                e.getMessage().contains("Incorrect enum value") ||
                e.getMessage().contains("GenericJDBCException")) {
                errorResponse.put("error", "Database configuration issue: The selected caregiver type is not supported. Please contact the administrator to update the database schema.");
                errorResponse.put("details", "This error occurs when the database enum constraint doesn't include the new caregiver types (CHILD_CARE, ELDER_CARE). Please run the database migration script.");
            } else {
                errorResponse.put("error", "Failed to submit application: " + e.getMessage());
            }
            
            errorResponse.put("timestamp", new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Submit application with files
    @PostMapping(value = "/apply-with-files", consumes = "multipart/form-data")
    public ResponseEntity<?> submitApplicationWithFiles(
            @RequestHeader("User-ID") Long userId,
            @RequestPart("applicationData") String applicationDataStr, 
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            System.out.println("=== Submitting Application with Files ===");
            System.out.println("User ID: " + userId);
            System.out.println("Files: " + (files != null ? files.size() : 0));
            
            Map<String, Object> applicationData = new ObjectMapper().readValue(applicationDataStr, HashMap.class);
            System.out.println("DEBUG FORM: Email from form = " + applicationData.get("email"));
            
            // Parse application data (same as above)
            Caregiver caregiverData = new Caregiver();
            caregiverData.setCaregiverType(Caregiver.CaregiverType.valueOf((String) applicationData.get("caregiverType")));
            caregiverData.setAddress((String) applicationData.get("address"));
            caregiverData.setCity((String) applicationData.get("city"));
            caregiverData.setState((String) applicationData.get("state"));
            caregiverData.setZipCode((String) applicationData.get("zipCode"));
            
            if (applicationData.get("dateOfBirth") != null) {
                LocalDate dateOfBirth = LocalDate.parse((String) applicationData.get("dateOfBirth"));
                // Validate that date of birth is in the past
                if (dateOfBirth.isAfter(LocalDate.now()) || dateOfBirth.equals(LocalDate.now())) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Date of birth must be a date in the past");
                    errorResponse.put("timestamp", new Date());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
                caregiverData.setDateOfBirth(dateOfBirth);
            }
            
            caregiverData.setSsn((String) applicationData.get("ssn"));
            caregiverData.setEmergencyContactName((String) applicationData.get("emergencyContactName"));
            caregiverData.setEmergencyContactPhone((String) applicationData.get("emergencyContactPhone"));
            caregiverData.setExperience((String) applicationData.get("experience"));
            caregiverData.setCertifications((String) applicationData.get("certifications"));
            caregiverData.setAvailability((String) applicationData.get("availability"));
            caregiverData.setSpecializations((String) applicationData.get("specializations"));
            caregiverData.setApplicationEmail((String) applicationData.get("email"));
            
            Caregiver savedApplication = caregiverService.createCaregiverApplication(userId, caregiverData, files);
            return ResponseEntity.ok(savedApplication);
        } catch (Exception e) {
            System.out.println("Error submitting application with files: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            
            // Check for database enum constraint error
            if (e.getMessage().contains("Data truncation") || 
                e.getMessage().contains("Incorrect enum value") ||
                e.getMessage().contains("GenericJDBCException")) {
                errorResponse.put("error", "Database configuration issue: The selected caregiver type is not supported. Please contact the administrator to update the database schema.");
                errorResponse.put("details", "This error occurs when the database enum constraint doesn't include the new caregiver types (CHILD_CARE, ELDER_CARE). Please run the database migration script.");
            } else {
                errorResponse.put("error", "Failed to submit application: " + e.getMessage());
            }
            
            errorResponse.put("timestamp", new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get all applications for logged-in user
    @GetMapping("/my-applications")
    public ResponseEntity<?> getUserApplications(@RequestHeader("User-ID") Long userId) {
        try {
            List<Caregiver> applications = caregiverService.getCaregiverApplicationsByUserId(userId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to load applications: " + e.getMessage()));
        }
    }

    // Get specific application by ID (user-specific)
    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplicationById(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long userId) {
        try {
            Caregiver application = caregiverService.getCaregiverApplicationById(applicationId, userId);
            if (application != null) {
                return ResponseEntity.ok(application);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to load application: " + e.getMessage()));
        }
    }

    // Update application (only if pending and owned by user)
    @PutMapping("/{applicationId}")
    public ResponseEntity<?> updateApplication(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long userId,
            @RequestBody Caregiver updatedData) {
        try {
            Caregiver updated = caregiverService.updateCaregiverApplication(applicationId, userId, updatedData);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Application cannot be updated (not found, not pending, or not owned by user)"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update application: " + e.getMessage()));
        }
    }

    // Delete application (only if pending and owned by user)
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<?> deleteApplication(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long userId) {
        try {
            boolean deleted = caregiverService.deleteCaregiverApplication(applicationId, userId);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Application deleted successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Application cannot be deleted (not found, not pending, or not owned by user)"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete application: " + e.getMessage()));
        }
    }

    // Upload files for application
    @PostMapping("/{applicationId}/files")
    public ResponseEntity<?> uploadFiles(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long userId,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            System.out.println("=== File Upload ===");
            System.out.println("Application ID: " + applicationId);
            System.out.println("User ID: " + userId);
            System.out.println("Files: " + (files != null ? files.size() : 0));
            
            Caregiver application = caregiverService.uploadFiles(applicationId, userId, files);
            if (application != null) {
                return ResponseEntity.ok(application);
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cannot upload files (application not found or not owned by user)"));
            }
        } catch (Exception e) {
            System.out.println("Error uploading files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload files: " + e.getMessage()));
        }
    }

    // Dashboard data for user
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData(@RequestHeader("User-ID") Long userId) {
        try {
            List<Caregiver> applications = caregiverService.getCaregiverApplicationsByUserId(userId);
            Long applicationCount = caregiverService.getApplicationCountByUserId(userId);
            
            // Calculate stats
            long pendingCount = applications.stream()
                .filter(app -> app.getStatus() == Caregiver.ApplicationStatus.PENDING).count();
            long approvedCount = applications.stream()
                .filter(app -> app.getStatus() == Caregiver.ApplicationStatus.APPROVED).count();
            long rejectedCount = applications.stream()
                .filter(app -> app.getStatus() == Caregiver.ApplicationStatus.REJECTED).count();
            
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("applications", applications);
            dashboard.put("stats", Map.of(
                "total", applicationCount,
                "pending", pendingCount,
                "approved", approvedCount,
                "rejected", rejectedCount
            ));
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to load dashboard: " + e.getMessage()));
        }
    }

    // Export application to PDF
    @GetMapping("/{applicationId}/export")
    public ResponseEntity<byte[]> exportToPdf(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long userId) throws DocumentException {
        try {
            // Verify ownership
            Caregiver application = caregiverService.getCaregiverApplicationById(applicationId, userId);
            if (application == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] pdf = caregiverService.exportToPdf(applicationId);
            if (pdf != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "caregiver-application-" + applicationId + ".pdf");
                return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get QR code for approved application
    @GetMapping("/{applicationId}/qrcode")
    public ResponseEntity<byte[]> getQrCode(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long userId) throws WriterException, IOException {
        try {
            // Verify ownership
            Caregiver application = caregiverService.getCaregiverApplicationById(applicationId, userId);
            if (application == null) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] qrCode = caregiverService.getQrCode(applicationId);
            if (qrCode != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin endpoints (for managing applications)
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllApplications(@RequestHeader("User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Admin access required"));
        }
        try {
            List<Caregiver> applications = caregiverService.getAllCaregivers();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to load applications: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPendingApplications(@RequestHeader("User-Role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Admin access required"));
        }
        try {
            List<Caregiver> pendingApplications = caregiverService.getPendingApplications();
            return ResponseEntity.ok(pendingApplications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to load pending applications: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/{applicationId}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long adminUserId,
            @RequestHeader("User-Role") String role,
            @RequestBody(required = false) Map<String, String> requestBody) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Admin access required"));
        }
        try {
            String notes = requestBody != null ? requestBody.get("notes") : null;
            Caregiver approved = caregiverService.approveApplication(applicationId, adminUserId, notes);
            if (approved != null) {
                return ResponseEntity.ok(approved);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to approve application: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/{applicationId}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable Long applicationId,
            @RequestHeader("User-ID") Long adminUserId,
            @RequestHeader("User-Role") String role,
            @RequestBody(required = false) Map<String, String> requestBody) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Admin access required"));
        }
        try {
            String notes = requestBody != null ? requestBody.get("notes") : null;
            Caregiver rejected = caregiverService.rejectApplication(applicationId, adminUserId, notes);
            if (rejected != null) {
                return ResponseEntity.ok(rejected);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to reject application: " + e.getMessage()));
        }
    }

    // Admin endpoint to update existing document types
    @PutMapping("/admin/update-document-types")
    public ResponseEntity<?> updateDocumentTypes(
            @RequestParam String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Admin access required"));
        }
        try {
            int updatedCount = caregiverService.updateExistingDocumentTypes();
            return ResponseEntity.ok(Map.of(
                "message", "Document types updated successfully",
                "updatedCount", updatedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update document types: " + e.getMessage()));
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getCaregiverProfileById(@PathVariable Long userId, HttpServletRequest request) {
        try {
            String userIdHeader = request.getHeader("User-ID");
            if (userIdHeader == null || !userIdHeader.equals(userId.toString())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }
            
            // First, try to find caregiver by userId
            Optional<Caregiver> caregiver = caregiverService.findByUserId(userId);
            
            // If not found by userId, try to find any caregiver application for this user
            if (!caregiver.isPresent()) {
                List<Caregiver> userCaregivers = caregiverService.findAllByUserId(userId);
                if (!userCaregivers.isEmpty()) {
                    // Get the most recent approved caregiver, or the first one if none approved
                    caregiver = userCaregivers.stream()
                    .filter(c -> c.getStatus() == Caregiver.ApplicationStatus.APPROVED)

                            .findFirst()
                            .or(() -> userCaregivers.stream().findFirst());
                }
            }
            
            if (caregiver.isPresent()) {
                Caregiver cg = caregiver.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", cg.getId());
                response.put("name", cg.getName());
                response.put("email", cg.getEmail());
                response.put("caregiverType", cg.getCaregiverType());
                response.put("careType", cg.getCaregiverType()); // Fallback for frontend compatibility
                response.put("registrationDate", cg.getApplicationDate());
                response.put("applicationDate", cg.getApplicationDate());
                response.put("status", cg.getStatus());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No caregiver profile found for this user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching caregiver profile: " + e.getMessage());
        }
    }
}
