-- CareNet Database Schema (Corrected)
-- Drop existing tables if they exist
DROP TABLE IF EXISTS documents;
DROP TABLE IF EXISTS caregivers;
DROP TABLE IF EXISTS users;

-- Create Users table (for authentication)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CAREGIVER') NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create Caregivers table (for applications)
CREATE TABLE caregivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    caregiver_type ENUM('PET_CARE', 'HUMAN_CARE') NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    date_of_birth DATE,
    ssn VARCHAR(20),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    experience TEXT,
    certifications TEXT,
    availability TEXT,
    specializations TEXT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    application_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    reviewed_by BIGINT NULL,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Create Documents table
CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    caregiver_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (caregiver_id) REFERENCES caregivers(id) ON DELETE CASCADE
);

-- Insert Sample Users
INSERT INTO users (email, password, role, first_name, last_name, phone) VALUES
('admin@carenet.com', 'admin123', 'ADMIN', 'System', 'Administrator', '+1-555-0001'),
('john.doe@gmail.com', 'password123', 'CAREGIVER', 'John', 'Doe', '+1-555-0101'),
('jane.smith@gmail.com', 'password123', 'CAREGIVER', 'Jane', 'Smith', '+1-555-0102'),
('mike.wilson@gmail.com', 'password123', 'CAREGIVER', 'Mike', 'Wilson', '+1-555-0103'),
('sarah.johnson@gmail.com', 'password123', 'CAREGIVER', 'Sarah', 'Johnson', '+1-555-0104'),
('david.brown@gmail.com', 'password123', 'CAREGIVER', 'David', 'Brown', '+1-555-0105');

-- Insert Sample Caregiver Applications
INSERT INTO caregivers (user_id, caregiver_type, address, city, state, zip_code, date_of_birth, emergency_contact_name, emergency_contact_phone, experience, certifications, availability, specializations, status) VALUES
-- John Doe - Human Care Applications
(2, 'HUMAN_CARE', '123 Main St', 'New York', 'NY', '10001', '1990-05-15', 'Mary Doe', '+1-555-1001', 'I have 5 years of experience caring for elderly patients in nursing homes and private residences. Specialized in dementia care and mobility assistance.', 'CNA, CPR, First Aid', 'Monday to Friday, 8AM to 6PM', 'Elderly Care, Dementia Care, Mobility Assistance', 'APPROVED'),
(2, 'HUMAN_CARE', '123 Main St', 'New York', 'NY', '10001', '1990-05-15', 'Mary Doe', '+1-555-1001', 'Applying for additional certification in pediatric care to expand my services.', 'CNA, CPR, First Aid, Pediatric Care Training', 'Weekends, 10AM to 8PM', 'Pediatric Care, Special Needs Children', 'PENDING'),

-- Jane Smith - Pet Care Applications  
(3, 'PET_CARE', '456 Oak Avenue', 'Los Angeles', 'CA', '90210', '1988-08-20', 'Tom Smith', '+1-555-2001', 'Professional pet sitter with 3 years experience. I love all animals and have worked with dogs, cats, birds, and small mammals.', 'Pet First Aid, Animal Behavior Certification', 'Flexible schedule, available for overnight stays', 'Dog Walking, Pet Sitting, Overnight Care', 'APPROVED'),

-- Mike Wilson - Human Care Application
(4, 'HUMAN_CARE', '789 Pine Street', 'Chicago', 'IL', '60601', '1985-12-10', 'Lisa Wilson', '+1-555-3001', 'Former paramedic with 8 years of medical experience. Transitioning to private care to provide more personalized attention.', 'EMT-P, Advanced Cardiac Life Support, CNA', 'Available 24/7 for emergency calls', 'Medical Care, Emergency Response, Post-Surgery Care', 'PENDING'),

-- Sarah Johnson - Pet Care Application
(5, 'PET_CARE', '321 Elm Drive', 'Miami', 'FL', '33101', '1992-03-25', 'Robert Johnson', '+1-555-4001', 'Veterinary assistant with deep understanding of animal behavior and medical needs. Excellent with anxious or aggressive pets.', 'Veterinary Assistant License, Animal Behavior Specialist', 'Monday to Friday, 7AM to 7PM', 'Medical Pet Care, Behavioral Training, Exotic Animals', 'APPROVED'),

-- David Brown - Human Care Application  
(6, 'HUMAN_CARE', '654 Cedar Lane', 'Houston', 'TX', '77001', '1987-11-05', 'Karen Brown', '+1-555-5001', 'Compassionate caregiver specializing in end-of-life care and family support during difficult times.', 'Hospice Care Certification, Grief Counseling, CNA', 'Available for live-in arrangements', 'Hospice Care, Palliative Care, Family Support', 'REJECTED');

-- Insert Sample Documents
INSERT INTO documents (caregiver_id, document_type, file_name, file_path, file_size, mime_type) VALUES
-- Documents for John Doe's first application (id: 1)
(1, 'ID', 'john_doe_drivers_license.pdf', '/uploads/documents/1/john_doe_drivers_license.pdf', 1024000, 'application/pdf'),
(1, 'MEDICAL', 'john_doe_medical_certificate.pdf', '/uploads/documents/1/john_doe_medical_certificate.pdf', 856000, 'application/pdf'),
(1, 'LICENSE', 'john_doe_cna_license.pdf', '/uploads/documents/1/john_doe_cna_license.pdf', 512000, 'application/pdf'),
(1, 'BACKGROUND', 'john_doe_background_check.pdf', '/uploads/documents/1/john_doe_background_check.pdf', 2048000, 'application/pdf'),
(1, 'TRAINING', 'john_doe_cpr_certificate.pdf', '/uploads/documents/1/john_doe_cpr_certificate.pdf', 768000, 'application/pdf'),

-- Documents for Jane Smith's application (id: 3)
(3, 'ID', 'jane_smith_passport.pdf', '/uploads/documents/3/jane_smith_passport.pdf', 1200000, 'application/pdf'),
(3, 'MEDICAL', 'jane_smith_health_clearance.pdf', '/uploads/documents/3/jane_smith_health_clearance.pdf', 920000, 'application/pdf'),
(3, 'LICENSE', 'jane_smith_pet_care_license.pdf', '/uploads/documents/3/jane_smith_pet_care_license.pdf', 640000, 'application/pdf'),
(3, 'BACKGROUND', 'jane_smith_background_verification.pdf', '/uploads/documents/3/jane_smith_background_verification.pdf', 1800000, 'application/pdf'),
(3, 'TRAINING', 'jane_smith_animal_behavior_cert.pdf', '/uploads/documents/3/jane_smith_animal_behavior_cert.pdf', 890000, 'application/pdf');

-- Verify the data
SELECT 'Users Count' as Info, COUNT(*) as Count FROM users
UNION ALL
SELECT 'Caregivers Count', COUNT(*) FROM caregivers  
UNION ALL
SELECT 'Documents Count', COUNT(*) FROM documents;

-- Show sample data
SELECT u.email, u.role, u.first_name, u.last_name, 
       COUNT(c.id) as applications_count
FROM users u 
LEFT JOIN caregivers c ON u.id = c.user_id 
GROUP BY u.id, u.email, u.role, u.first_name, u.last_name
ORDER BY u.role, u.first_name;
