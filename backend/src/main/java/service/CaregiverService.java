package com.CareNet.CareNet.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.DocumentException;
import com.CareNet.CareNet.model.Caregiver;
import com.CareNet.CareNet.model.Document;
import com.CareNet.CareNet.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.CareNet.CareNet.repository.CaregiverRepository;
import com.CareNet.CareNet.repository.DocumentRepository;
import com.CareNet.CareNet.repository.UserRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CaregiverService {

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    // Create caregiver application for existing user only
    public Caregiver createCaregiverApplication(Long userId, Caregiver caregiverData, List<MultipartFile> files) throws IOException {
        // Find existing user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found. Please login first."));
        
        if (!user.getRole().equals(User.UserRole.CAREGIVER)) {
            throw new RuntimeException("Only caregivers can submit applications.");
        }
        
        // Create new application for the user
        Caregiver caregiver = new Caregiver(user, caregiverData.getCaregiverType());
        caregiver.setAddress(caregiverData.getAddress());
        caregiver.setCity(caregiverData.getCity());
        caregiver.setState(caregiverData.getState());
        caregiver.setZipCode(caregiverData.getZipCode());
        caregiver.setDateOfBirth(caregiverData.getDateOfBirth());
        caregiver.setSsn(caregiverData.getSsn());
        caregiver.setEmergencyContactName(caregiverData.getEmergencyContactName());
        caregiver.setEmergencyContactPhone(caregiverData.getEmergencyContactPhone());
        caregiver.setExperience(caregiverData.getExperience());
        caregiver.setCertifications(caregiverData.getCertifications());
        caregiver.setAvailability(caregiverData.getAvailability());
        caregiver.setSpecializations(caregiverData.getSpecializations());
        caregiver.setApplicationEmail(caregiverData.getApplicationEmail());
        caregiver.setStatus(Caregiver.ApplicationStatus.PENDING);
        
        Caregiver savedCaregiver = caregiverRepository.save(caregiver);

        // Handle file uploads
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Document document = new Document();
                document.setDocumentType(determineDocumentType(file.getOriginalFilename()));
                document.setFileName(file.getOriginalFilename());
                document.setFilePath("/uploads/documents/" + savedCaregiver.getId() + "/" + file.getOriginalFilename());
                document.setFileSize(file.getSize());
                document.setMimeType(file.getContentType());
                document.setData(file.getBytes());
                document.setCaregiver(savedCaregiver);
                documentRepository.save(document);
            }
        }
        
        // Send application submitted email
        try {
            String emailToSend = savedCaregiver.getApplicationEmail() != null && !savedCaregiver.getApplicationEmail().trim().isEmpty() 
                               ? savedCaregiver.getApplicationEmail() 
                               : user.getEmail();
            System.out.println("DEBUG EMAIL: Application Email = " + savedCaregiver.getApplicationEmail());
            System.out.println("DEBUG EMAIL: User Email = " + user.getEmail());
            System.out.println("DEBUG EMAIL: Sending email to = " + emailToSend);
            emailService.sendApplicationSubmittedEmail(
                emailToSend,
                user.getFirstName() + " " + user.getLastName(),
                savedCaregiver.getId()
            );
        } catch (Exception e) {
            System.err.println("Failed to send application submitted email: " + e.getMessage());
            // Don't fail the application submission if email fails
        }

        return savedCaregiver;
    }

    // Get all applications for a specific user
    public List<Caregiver> getCaregiverApplicationsByUserId(Long userId) {
        return caregiverRepository.findByUserIdOrderByApplicationDateDesc(userId);
    }

    // Get specific application by ID (with user verification)
    public Caregiver getCaregiverApplicationById(Long applicationId, Long userId) {
        Caregiver caregiver = caregiverRepository.findById(applicationId).orElse(null);
        if (caregiver != null && caregiver.getUser().getId().equals(userId)) {
            return caregiver;
        }
        return null; // Not found or doesn't belong to user
    }

    public Caregiver getCaregiverById(Long id) {
        return caregiverRepository.findById(id).orElse(null);
    }

    public List<Caregiver> getAllCaregivers() {
        return caregiverRepository.findAll();
    }

    public List<Caregiver> getPendingApplications() {
        return caregiverRepository.findPendingApplications();
    }

    // Update application (only by owner)
    public Caregiver updateCaregiverApplication(Long applicationId, Long userId, Caregiver updatedData) {
        Caregiver existingCaregiver = getCaregiverApplicationById(applicationId, userId);
        if (existingCaregiver != null && existingCaregiver.getStatus() == Caregiver.ApplicationStatus.PENDING) {
            // Update allowed fields
            existingCaregiver.setAddress(updatedData.getAddress());
            existingCaregiver.setCity(updatedData.getCity());
            existingCaregiver.setState(updatedData.getState());
            existingCaregiver.setZipCode(updatedData.getZipCode());
            existingCaregiver.setDateOfBirth(updatedData.getDateOfBirth());
            existingCaregiver.setEmergencyContactName(updatedData.getEmergencyContactName());
            existingCaregiver.setEmergencyContactPhone(updatedData.getEmergencyContactPhone());
            existingCaregiver.setExperience(updatedData.getExperience());
            existingCaregiver.setCertifications(updatedData.getCertifications());
            existingCaregiver.setAvailability(updatedData.getAvailability());
            existingCaregiver.setSpecializations(updatedData.getSpecializations());
            
            return caregiverRepository.save(existingCaregiver);
        }
        return null;
    }

    // Delete application (only by owner and only if pending)
    public boolean deleteCaregiverApplication(Long applicationId, Long userId) {
        Caregiver caregiver = getCaregiverApplicationById(applicationId, userId);
        if (caregiver != null && caregiver.getStatus() == Caregiver.ApplicationStatus.PENDING) {
            caregiverRepository.deleteById(applicationId);
            return true;
        }
        return false;
    }

    // Admin functions
    public Caregiver approveApplication(Long applicationId, Long adminUserId, String notes) {
        Caregiver caregiver = caregiverRepository.findById(applicationId).orElse(null);
        if (caregiver != null) {
            caregiver.setStatus(Caregiver.ApplicationStatus.APPROVED);
            caregiver.setReviewedAt(LocalDateTime.now());
            caregiver.setReviewedBy(adminUserId);
            caregiver.setNotes(notes);
            Caregiver savedCaregiver = caregiverRepository.save(caregiver);
            
            // Send approval email
            try {
                String emailToSend = caregiver.getApplicationEmail() != null && !caregiver.getApplicationEmail().trim().isEmpty() 
                                   ? caregiver.getApplicationEmail() 
                                   : caregiver.getUser().getEmail();
                System.out.println("DEBUG APPROVE EMAIL: Application Email = " + caregiver.getApplicationEmail());
                System.out.println("DEBUG APPROVE EMAIL: User Email = " + caregiver.getUser().getEmail());
                System.out.println("DEBUG APPROVE EMAIL: Sending approval email to = " + emailToSend);
                emailService.sendApplicationApprovedEmail(
                    emailToSend,
                    caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName(),
                    applicationId,
                    notes
                );
            } catch (Exception e) {
                System.err.println("Failed to send application approved email: " + e.getMessage());
                // Don't fail the approval if email fails
            }
            
            return savedCaregiver;
        }
        return null;
    }

    public Caregiver rejectApplication(Long applicationId, Long adminUserId, String notes) {
        Caregiver caregiver = caregiverRepository.findById(applicationId).orElse(null);
        if (caregiver != null) {
            caregiver.setStatus(Caregiver.ApplicationStatus.REJECTED);
            caregiver.setReviewedAt(LocalDateTime.now());
            caregiver.setReviewedBy(adminUserId);
            caregiver.setNotes(notes);
            Caregiver savedCaregiver = caregiverRepository.save(caregiver);
            
            // Send rejection email
            try {
                String emailToSend = caregiver.getApplicationEmail() != null && !caregiver.getApplicationEmail().trim().isEmpty() 
                                   ? caregiver.getApplicationEmail() 
                                   : caregiver.getUser().getEmail();
                System.out.println("DEBUG REJECT EMAIL: Application Email = " + caregiver.getApplicationEmail());
                System.out.println("DEBUG REJECT EMAIL: User Email = " + caregiver.getUser().getEmail());
                System.out.println("DEBUG REJECT EMAIL: Sending rejection email to = " + emailToSend);
                emailService.sendApplicationRejectedEmail(
                    emailToSend,
                    caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName(),
                    applicationId,
                    notes
                );
            } catch (Exception e) {
                System.err.println("Failed to send application rejected email: " + e.getMessage());
                // Don't fail the rejection if email fails
            }
            
            return savedCaregiver;
        }
        return null;
    }

    public Caregiver uploadFiles(Long applicationId, Long userId, List<MultipartFile> files) throws IOException {
        Caregiver caregiver = getCaregiverApplicationById(applicationId, userId);
        if (caregiver != null && files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Document document = new Document();
                document.setDocumentType(determineDocumentType(file.getOriginalFilename()));
                document.setFileName(file.getOriginalFilename());
                document.setFilePath("/uploads/documents/" + applicationId + "/" + file.getOriginalFilename());
                document.setFileSize(file.getSize());
                document.setMimeType(file.getContentType());
                document.setData(file.getBytes());
                document.setCaregiver(caregiver);
                documentRepository.save(document);
            }
            return caregiver;
        }
        return null;
    }

    // Determine document type from filename with enhanced logic
    private String determineDocumentType(String filename) {
        if (filename == null) {
            System.out.println("DEBUG: filename is null, returning OTHER");
            return "OTHER";
        }
        
        String lowercaseName = filename.toLowerCase();
        String extension = "";
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = filename.substring(lastDotIndex).toLowerCase();
        }
        
        System.out.println("DEBUG: Processing filename: " + filename + ", lowercase: " + lowercaseName);
        
        // ID Documents
        if (lowercaseName.contains("id") || lowercaseName.contains("identity") || 
            lowercaseName.contains("license") || lowercaseName.contains("licence") || 
            lowercaseName.contains("passport") || lowercaseName.contains("driver") ||
            lowercaseName.contains("national") || lowercaseName.contains("government")) {
            System.out.println("DEBUG: Detected ID document for: " + filename);
            return "ID";
        } 
        // Medical Documents
        else if (lowercaseName.contains("medical") || lowercaseName.contains("health") || 
                 lowercaseName.contains("doctor") || lowercaseName.contains("hospital") || 
                 lowercaseName.contains("clinic") || lowercaseName.contains("physical") ||
                 lowercaseName.contains("exam") || lowercaseName.contains("clearance")) {
            System.out.println("DEBUG: Detected MEDICAL document for: " + filename);
            return "MEDICAL";
        } 
        // Background Check Documents
        else if (lowercaseName.contains("background") || lowercaseName.contains("criminal") || 
                 lowercaseName.contains("police") || lowercaseName.contains("clearance") ||
                 lowercaseName.contains("record") || lowercaseName.contains("check")) {
            System.out.println("DEBUG: Detected BACKGROUND document for: " + filename);
            return "BACKGROUND";
        } 
        // Training/Education Documents
        else if (lowercaseName.contains("training") || lowercaseName.contains("certificate") || 
                 lowercaseName.contains("certification") || lowercaseName.contains("cpr") || 
                 lowercaseName.contains("first") || lowercaseName.contains("aid") ||
                 lowercaseName.contains("course") || lowercaseName.contains("diploma") ||
                 lowercaseName.contains("degree") || lowercaseName.contains("education")) {
            System.out.println("DEBUG: Detected TRAINING document for: " + filename);
            return "TRAINING";
        }
        // Insurance Documents
        else if (lowercaseName.contains("insurance") || lowercaseName.contains("liability") ||
                 lowercaseName.contains("coverage") || lowercaseName.contains("policy")) {
            System.out.println("DEBUG: Detected INSURANCE document for: " + filename);
            return "INSURANCE";
        }
        // Reference Documents
        else if (lowercaseName.contains("reference") || lowercaseName.contains("recommendation") ||
                 lowercaseName.contains("employer") || lowercaseName.contains("work")) {
            System.out.println("DEBUG: Detected REFERENCE document for: " + filename);
            return "REFERENCE";
        }
        // If we can't determine from filename, try to guess from file extension
        else if (extension.equals(".pdf") || extension.equals(".doc") || extension.equals(".docx")) {
            // For generic document files, check if it's one of the first few files uploaded
            // This is a fallback - in real applications, you'd want users to specify the type
            System.out.println("DEBUG: Defaulting to DOCUMENT for: " + filename + " with extension: " + extension);
            return "DOCUMENT";
        }
        else {
            System.out.println("DEBUG: Defaulting to OTHER for: " + filename + " with extension: " + extension);
            return "OTHER";
        }
    }

    private String getDocumentTypeDisplayName(String type) {
        switch (type) {
            case "ID":
                return "Identification Document";
            case "MEDICAL":
                return "Medical Certificate";
            case "BACKGROUND":
                return "Background Check";
            case "TRAINING":
                return "Training Certificate";
            case "INSURANCE":
                return "Insurance Document";
            case "REFERENCE":
                return "Reference Letter";
            case "DOCUMENT":
                return "General Document";
            case "OTHER":
            default:
                return "Other Document";
        }
    }

    private String getStatusIcon(String status) {
        switch (status) {
            case "APPROVED":
                return "[✓]";
            case "REJECTED":
                return "[✗]";
            case "PENDING":
            default:
                return "[⏳]";
        }
    }

    public byte[] exportToPdf(Long id) throws DocumentException {
        Caregiver caregiver = caregiverRepository.findById(id).orElse(null);
        if (caregiver != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(document, baos);

            document.open();
            
            // Title
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Caregiver Application Report", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.lowagie.text.Paragraph("\n"));
            
            // Basic Information
            document.add(new com.lowagie.text.Paragraph("PERSONAL INFORMATION", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
            document.add(new com.lowagie.text.Paragraph("Name: " + caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName()));
            document.add(new com.lowagie.text.Paragraph("Email: " + caregiver.getUser().getEmail()));
            if (caregiver.getUser().getPhone() != null) {
                document.add(new com.lowagie.text.Paragraph("Phone: " + caregiver.getUser().getPhone()));
            }
            document.add(new com.lowagie.text.Paragraph("Caregiver Type: " + caregiver.getCaregiverType()));
            document.add(new com.lowagie.text.Paragraph("\n"));
            
            // Address Information
            if (caregiver.getAddress() != null) {
                document.add(new com.lowagie.text.Paragraph("ADDRESS INFORMATION", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
                document.add(new com.lowagie.text.Paragraph("Address: " + caregiver.getAddress()));
                if (caregiver.getCity() != null) {
                    document.add(new com.lowagie.text.Paragraph("City: " + caregiver.getCity()));
                }
                if (caregiver.getState() != null) {
                    document.add(new com.lowagie.text.Paragraph("State: " + caregiver.getState()));
                }
                if (caregiver.getZipCode() != null) {
                    document.add(new com.lowagie.text.Paragraph("ZIP Code: " + caregiver.getZipCode()));
                }
                document.add(new com.lowagie.text.Paragraph("\n"));
            }
            
            // Professional Information
            document.add(new com.lowagie.text.Paragraph("PROFESSIONAL INFORMATION", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
            if (caregiver.getExperience() != null) {
                document.add(new com.lowagie.text.Paragraph("Experience: " + caregiver.getExperience()));
            }
            if (caregiver.getCertifications() != null) {
                document.add(new com.lowagie.text.Paragraph("Certifications: " + caregiver.getCertifications()));
            }
            if (caregiver.getSpecializations() != null) {
                document.add(new com.lowagie.text.Paragraph("Specializations: " + caregiver.getSpecializations()));
            }
            if (caregiver.getAvailability() != null) {
                document.add(new com.lowagie.text.Paragraph("Availability: " + caregiver.getAvailability()));
            }
            document.add(new com.lowagie.text.Paragraph("\n"));
            
            // Emergency Contact
            if (caregiver.getEmergencyContactName() != null) {
                document.add(new com.lowagie.text.Paragraph("EMERGENCY CONTACT", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
                document.add(new com.lowagie.text.Paragraph("Name: " + caregiver.getEmergencyContactName()));
                if (caregiver.getEmergencyContactPhone() != null) {
                    document.add(new com.lowagie.text.Paragraph("Phone: " + caregiver.getEmergencyContactPhone()));
                }
                document.add(new com.lowagie.text.Paragraph("\n"));
            }
            
            // Application Status
            document.add(new com.lowagie.text.Paragraph("APPLICATION STATUS", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
            document.add(new com.lowagie.text.Paragraph("Status: " + caregiver.getStatus()));
            if (caregiver.getApplicationDate() != null) {
                document.add(new com.lowagie.text.Paragraph("Application Date: " + caregiver.getApplicationDate()));
            }
            if (caregiver.getReviewedAt() != null) {
                document.add(new com.lowagie.text.Paragraph("Reviewed Date: " + caregiver.getReviewedAt()));
            }
            document.add(new com.lowagie.text.Paragraph("\n"));
            
            // Documents Status
            if (caregiver.getDocuments() != null && !caregiver.getDocuments().isEmpty()) {
                document.add(new com.lowagie.text.Paragraph("DOCUMENT STATUS", new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD)));
                
                int totalDocs = caregiver.getDocuments().size();
                int approvedDocs = (int) caregiver.getDocuments().stream().filter(d -> "APPROVED".equals(d.getStatus())).count();
                int rejectedDocs = (int) caregiver.getDocuments().stream().filter(d -> "REJECTED".equals(d.getStatus())).count();
                int pendingDocs = totalDocs - approvedDocs - rejectedDocs;
                
                document.add(new com.lowagie.text.Paragraph("Summary: " + approvedDocs + " approved, " + rejectedDocs + " rejected, " + pendingDocs + " pending (Total: " + totalDocs + ")"));
                document.add(new com.lowagie.text.Paragraph("\n"));
                
                for (Document doc : caregiver.getDocuments()) {
                    String status = doc.getStatus() != null ? doc.getStatus() : "PENDING";
                    String docTypeDisplay = getDocumentTypeDisplayName(doc.getDocumentType());
                    String statusIcon = getStatusIcon(status);
                    document.add(new com.lowagie.text.Paragraph(statusIcon + " " + docTypeDisplay + " (" + doc.getFileName() + "): " + status));
                    if (doc.getUploadedAt() != null) {
                        document.add(new com.lowagie.text.Paragraph("   Uploaded: " + doc.getUploadedAt().toString()));
                    }
                }
                document.add(new com.lowagie.text.Paragraph("\n"));
            }
            
            // Footer
            document.add(new com.lowagie.text.Paragraph("Generated on: " + java.time.LocalDateTime.now().toString(), new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.ITALIC)));
            
            document.close();
            return baos.toByteArray();
        }
        return null;
    }

    public byte[] getQrCode(Long id) throws WriterException, IOException {
        Caregiver caregiver = caregiverRepository.findById(id).orElse(null);
        if (caregiver != null && caregiver.getStatus().equals(Caregiver.ApplicationStatus.APPROVED)) {
            String qrCodeText = "Caregiver ID: " + caregiver.getId() + "\n" + 
                    "Name: " + caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName() + "\n" +
                    "Type: " + caregiver.getCaregiverType();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 250, 250);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        }
        return null;
    }

    public Long getApplicationCountByUserId(Long userId) {
        return caregiverRepository.countByUserId(userId);
    }

    // Method to update existing document types based on filenames
    public int updateExistingDocumentTypes() {
        List<Document> allDocuments = documentRepository.findAll();
        int updatedCount = 0;
        
        System.out.println("DEBUG: Found " + allDocuments.size() + " documents to process");
        
        for (Document doc : allDocuments) {
            String oldType = doc.getDocumentType();
            String newType = determineDocumentType(doc.getFileName());
            
            if (!oldType.equals(newType)) {
                System.out.println("DEBUG: Updating document " + doc.getId() + " from type " + oldType + " to " + newType + " (filename: " + doc.getFileName() + ")");
                doc.setDocumentType(newType);
                documentRepository.save(doc);
                updatedCount++;
            } else {
                System.out.println("DEBUG: Document " + doc.getId() + " already has correct type " + oldType + " (filename: " + doc.getFileName() + ")");
            }
        }
        
        System.out.println("DEBUG: Updated " + updatedCount + " documents");
        return updatedCount;
    }

    public Optional<Caregiver> findByUserId(Long userId) {
        return caregiverRepository.findByUserId(userId);
    }
    
    public List<Caregiver> findAllByUserId(Long userId) {
        return caregiverRepository.findAllByUserId(userId);
    }

    public Optional<Caregiver> findByEmail(String email) {
        return caregiverRepository.findByEmail(email);
    }
}