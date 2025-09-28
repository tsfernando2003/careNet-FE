package com.CareNet.CareNet.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "caregivers")
public class Caregiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "caregiver_type", nullable = false)
    private CaregiverType caregiverType;

    private String address;
    private String city;
    private String state;
    @Column(name = "zip_code")
    private String zipCode;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    private String ssn;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    private String experience;
    private String certifications;
    private String availability;
    private String specializations;
    
    @Column(name = "application_email")
    private String applicationEmail;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @Column(name = "application_date")
    private LocalDateTime applicationDate = LocalDateTime.now();
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "reviewed_by")
    private Long reviewedBy;
    
    private String notes;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Document> documents;
    
    // Many-to-One relationship with User (multiple applications per user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public enum CaregiverType {
        PET_CARE, CHILD_CARE, ELDER_CARE, HUMAN_CARE // HUMAN_CARE temporarily kept for backward compatibility
    }

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }

    // Constructors
    public Caregiver() {}

    public Caregiver(User user, CaregiverType caregiverType) {
        this.user = user;
        this.caregiverType = caregiverType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public CaregiverType getCaregiverType() {
        return caregiverType;
    }

    public void setCaregiverType(CaregiverType caregiverType) {
        this.caregiverType = caregiverType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getSpecializations() {
        return specializations;
    }

    public void setSpecializations(String specializations) {
        this.specializations = specializations;
    }

    public String getApplicationEmail() {
        return applicationEmail;
    }

    public void setApplicationEmail(String applicationEmail) {
        this.applicationEmail = applicationEmail;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Long reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @PreUpdate
    public void preUpdate() {
        // Update reviewedAt when status changes
        if (this.status != ApplicationStatus.PENDING && this.reviewedAt == null) {
            this.reviewedAt = LocalDateTime.now();
        }
    }

    // Legacy methods for backward compatibility
    public String getFirstName() {
        return user != null ? user.getFirstName() : null;
    }

    public void setFirstName(String firstName) {
        // Legacy method - first name is stored in User entity
    }

    public String getLastName() {
        return user != null ? user.getLastName() : null;
    }

    public void setLastName(String lastName) {
        // Legacy method - last name is stored in User entity
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public void setEmail(String email) {
        // Legacy method - email is stored in User entity
    }

    public String getPhone() {
        return user != null ? user.getPhone() : null;
    }

    public void setPhone(String phone) {
        // Legacy method - phone is stored in User entity
    }

    public String getEmergencyContact() {
        return this.emergencyContactName + " - " + this.emergencyContactPhone;
    }

    public void setEmergencyContact(String emergencyContact) {
        // Parse legacy format "Name - Phone"
        if (emergencyContact != null && emergencyContact.contains(" - ")) {
            String[] parts = emergencyContact.split(" - ");
            this.emergencyContactName = parts[0];
            this.emergencyContactPhone = parts.length > 1 ? parts[1] : null;
        }
    }

    // String status methods for backward compatibility
    public void setStatus(String status) {
        try {
            this.status = ApplicationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle legacy status values
            switch (status.toUpperCase()) {
                case "UNDER_REVIEW":
                    this.status = ApplicationStatus.PENDING;
                    break;
                case "VERIFIED":
                    this.status = ApplicationStatus.APPROVED;
                    break;
                default:
                    this.status = ApplicationStatus.PENDING;
            }
        }
    }

    public LocalDateTime getCreatedAt() {
        return this.applicationDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.applicationDate = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.reviewedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.reviewedAt = updatedAt;
    }

    // Date of birth string methods for backward compatibility
    public void setDateOfBirth(String dateString) {
        try {
            this.dateOfBirth = LocalDate.parse(dateString);
        } catch (Exception e) {
            // Handle invalid date formats
            this.dateOfBirth = null;
        }
    }

    // Add a safe getter so controller code that calls cg.getName() compiles.
    public String getName() {
        // Prefer explicit first/last name from associated User if available
        if (this.user != null) {
            String first = this.user.getFirstName();
            String last = this.user.getLastName();
            if (first != null && !first.trim().isEmpty() && last != null && !last.trim().isEmpty()) {
                return first.trim() + " " + last.trim();
            }
            if (first != null && !first.trim().isEmpty()) {
                return first.trim();
            }
            if (last != null && !last.trim().isEmpty()) {
                return last.trim();
            }
            // fallback to user's email if names are not present
            String userEmail = this.user.getEmail();
            if (userEmail != null && !userEmail.trim().isEmpty()) {
                return userEmail.trim();
            }
        }

        // Fallback to caregiver's applicationEmail if present
        if (this.applicationEmail != null && !this.applicationEmail.trim().isEmpty()) {
            return this.applicationEmail.trim();
        }

        return null;
    }
}
