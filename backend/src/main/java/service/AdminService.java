package com.CareNet.CareNet.service;

import com.CareNet.CareNet.model.Caregiver;
import com.CareNet.CareNet.model.Document;
import com.CareNet.CareNet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.CareNet.CareNet.repository.CaregiverRepository;
import com.CareNet.CareNet.repository.DocumentRepository;
import com.CareNet.CareNet.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class AdminService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CaregiverRepository caregiverRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CaregiverService caregiverService;
    
    @Autowired
    private EmailService emailService;

    public List<Caregiver> getAllCaregivers() {
        return caregiverRepository.findAll();
    }
    
    public List<Caregiver> getAllCaregivers(String search, String status) {
        List<Caregiver> caregivers = caregiverRepository.findAll();
        
        // Filter by search term if provided
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            caregivers = caregivers.stream()
                .filter(caregiver -> {
                    String firstName = caregiver.getUser() != null && caregiver.getUser().getFirstName() != null 
                        ? caregiver.getUser().getFirstName().toLowerCase() : "";
                    String lastName = caregiver.getUser() != null && caregiver.getUser().getLastName() != null 
                        ? caregiver.getUser().getLastName().toLowerCase() : "";
                    String email = caregiver.getUser() != null && caregiver.getUser().getEmail() != null 
                        ? caregiver.getUser().getEmail().toLowerCase() : "";
                    String phone = caregiver.getUser() != null && caregiver.getUser().getPhone() != null 
                        ? caregiver.getUser().getPhone().toLowerCase() : "";
                    String address = caregiver.getAddress() != null ? caregiver.getAddress().toLowerCase() : "";
                    
                    return firstName.contains(searchLower) || 
                           lastName.contains(searchLower) || 
                           email.contains(searchLower) || 
                           phone.contains(searchLower) || 
                           address.contains(searchLower);
                })
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Filter by status if provided
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            try {
                Caregiver.ApplicationStatus statusEnum = Caregiver.ApplicationStatus.valueOf(status.toUpperCase());
                caregivers = caregivers.stream()
                    .filter(caregiver -> caregiver.getStatus() == statusEnum)
                    .collect(java.util.stream.Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, return empty list or all caregivers
                System.err.println("Invalid status filter: " + status);
            }
        }
        
        return caregivers;
    }

    public void changeDocumentStatus(Long fileId, String status) {
        if (fileId == null || status == null) {
            return;
        }
        Document document = documentRepository.findById(fileId).orElse(null);
        if (document != null) {
            String oldStatus = document.getStatus();
            document.setStatus(status);
            documentRepository.save(document);
            
            // Send document rejection email when document status changes to REJECTED
            if ("REJECTED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(oldStatus)) {
                Caregiver caregiver = document.getCaregiver();
                if (caregiver != null) {
                    try {
                        // Determine which email to send to (form email or registered email)
                        String emailToSend = caregiver.getApplicationEmail() != null && !caregiver.getApplicationEmail().trim().isEmpty() 
                                           ? caregiver.getApplicationEmail() 
                                           : caregiver.getUser().getEmail();
                        
                        String applicantName = caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName();
                        
                        System.out.println("DEBUG DOCUMENT REJECT: Document Name = " + document.getFileName());
                        System.out.println("DEBUG DOCUMENT REJECT: Application Email = " + caregiver.getApplicationEmail());
                        System.out.println("DEBUG DOCUMENT REJECT: User Email = " + caregiver.getUser().getEmail());
                        System.out.println("DEBUG DOCUMENT REJECT: Sending document rejection email to = " + emailToSend);
                        
                        emailService.sendDocumentRejectedEmail(
                            emailToSend,
                            applicantName,
                            caregiver.getId(),
                            document.getFileName(),
                            "Document did not meet our requirements and needs to be resubmitted"
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to send document rejection email: " + e.getMessage());
                        // Don't fail the status change if email fails
                    }
                }
            }
            
            // Auto-rejection logic: If any document is rejected, reject the entire application
            if ("REJECTED".equalsIgnoreCase(status)) {
                Caregiver caregiver = document.getCaregiver();
                if (caregiver != null && caregiver.getStatus() != Caregiver.ApplicationStatus.REJECTED) {
                    caregiver.setStatus(Caregiver.ApplicationStatus.REJECTED);
                    caregiver.setReviewedAt(LocalDateTime.now());
                    caregiverRepository.save(caregiver);
                    System.out.println("Application automatically rejected due to document rejection for caregiver ID: " + caregiver.getId());
                }
            }
        }
    }

    public void verifyCaregiver(Long id) {
        if (id == null) {
            return;
        }
        Caregiver caregiver = caregiverRepository.findById(id).orElse(null);
        if (caregiver != null) {
            // Check if all documents are approved before allowing application approval
            List<Document> documents = caregiver.getDocuments();
            if (documents == null || documents.isEmpty()) {
                throw new RuntimeException("Cannot approve application: No documents uploaded");
            }
            
            boolean allDocumentsApproved = documents.stream()
                .allMatch(doc -> "APPROVED".equalsIgnoreCase(doc.getStatus()));
            
            if (!allDocumentsApproved) {
                throw new RuntimeException("Cannot approve application: All documents must be individually approved first");
            }
            
            // Use CaregiverService to handle approval with email
            caregiverService.approveApplication(id, 1L, "Application approved by admin");
        }
    }

    public void rejectCaregiver(Long id) {
        if (id == null) {
            return;
        }
        Caregiver caregiver = caregiverRepository.findById(id).orElse(null);
        if (caregiver != null) {
            // Use CaregiverService to handle rejection with email
            caregiverService.rejectApplication(id, 1L, "Application rejected by admin");
        }
    }

    public void updateCaregiverStatus(Long id, String status) {
        if (id == null || status == null) {
            return;
        }
        Caregiver caregiver = caregiverRepository.findById(id).orElse(null);
        if (caregiver != null) {
            // Convert string to enum
            try {
                Caregiver.ApplicationStatus enumStatus = Caregiver.ApplicationStatus.valueOf(status.toUpperCase());
                caregiver.setStatus(enumStatus);
                caregiver.setReviewedAt(LocalDateTime.now());
                caregiverRepository.save(caregiver);
            } catch (IllegalArgumentException e) {
                // Handle invalid status strings
                System.err.println("Invalid status: " + status);
            }
        }
    }

    public boolean deleteCaregiver(Long id) {
        if (id == null) {
            return false;
        }
        try {
            // First delete all associated documents
            Caregiver caregiver = caregiverRepository.findById(id).orElse(null);
            if (caregiver != null) {
                // Delete associated documents first
                documentRepository.deleteByCaregiver(caregiver);
                // Then delete the caregiver
                caregiverRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Document getDocumentById(Long id) {
        if (id == null) {
            return null;
        }
        return documentRepository.findById(id).orElse(null);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean caregiverExistsByEmail(String email) {
        return caregiverRepository.findByEmail(email).isPresent();
    }
    
    public Map<String, Object> createSampleCaregiverData(User user) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Create caregiver application
            Caregiver caregiver = new Caregiver();
            caregiver.setUser(user);
            // Set caregiver properties (now handled through User relationship)
            caregiver.setUser(user);
            caregiver.setCaregiverType(Caregiver.CaregiverType.ELDER_CARE);
            caregiver.setAddress("123 Care Street, Health City, HC 12345");
            caregiver.setDateOfBirth(java.time.LocalDate.of(1990, 5, 15));
            caregiver.setEmergencyContactName("Jane Doe");
            caregiver.setEmergencyContactPhone("+1-234-567-8901");
            caregiver.setExperience("5 years of experience in home healthcare");
            caregiver.setCertifications("CNA, CPR, First Aid");
            caregiver.setAvailability("Full-time, weekdays and weekends");
            caregiver.setStatus(Caregiver.ApplicationStatus.PENDING);
            caregiver.setApplicationDate(LocalDateTime.now().minusDays(7));
            caregiver.setReviewedAt(LocalDateTime.now().minusDays(1));
            
            Caregiver savedCaregiver = caregiverRepository.save(caregiver);
            
            // Create sample documents
            Document doc1 = new Document();
            doc1.setFileName("john_doe_id.pdf");
            doc1.setDocumentType("ID");
            doc1.setMimeType("application/pdf");
            doc1.setData("Sample PDF data for government ID".getBytes());
            doc1.setUploadedAt(LocalDateTime.now().minusDays(6));
            doc1.setStatus("PENDING"); // Set initial status
            doc1.setCaregiver(savedCaregiver);
            documentRepository.save(doc1);
            
            Document doc2 = new Document();
            doc2.setFileName("medical_cert.pdf");
            doc2.setDocumentType("MEDICAL");
            doc2.setMimeType("application/pdf");
            doc2.setData("Sample PDF data for medical certificate".getBytes());
            doc2.setUploadedAt(LocalDateTime.now().minusDays(5));
            doc2.setStatus("PENDING"); // Set initial status
            doc2.setCaregiver(savedCaregiver);
            documentRepository.save(doc2);
            
            Document doc3 = new Document();
            doc3.setFileName("nursing_license.jpg");
            doc3.setDocumentType("LICENSE");
            doc3.setMimeType("image/jpeg");
            doc3.setData("Sample image data for nursing license".getBytes());
            doc3.setUploadedAt(LocalDateTime.now().minusDays(4));
            doc3.setStatus("PENDING"); // Set initial status
            doc3.setCaregiver(savedCaregiver);
            documentRepository.save(doc3);
            
            result.put("success", true);
            result.put("message", "Sample caregiver data created successfully");
            result.put("caregiverId", savedCaregiver.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error creating sample data: " + e.getMessage());
        }
        
        return result;
    }
}