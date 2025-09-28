package com.CareNet.CareNet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // Send application submission confirmation email
    public void sendApplicationSubmittedEmail(String toEmail, String applicantName, Long applicationId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("CareNet - Application Submitted Successfully");
            
            String htmlContent = buildApplicationSubmittedEmailContent(applicantName, applicationId);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("Application submitted email sent to: " + toEmail);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send application submitted email to: " + toEmail);
            e.printStackTrace();
        }
    }

    // Send application approval email
    public void sendApplicationApprovedEmail(String toEmail, String applicantName, Long applicationId, String notes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🎉 CareNet - Application Approved!");
            
            String htmlContent = buildApplicationApprovedEmailContent(applicantName, applicationId, notes);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("Application approved email sent to: " + toEmail);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send application approved email to: " + toEmail);
            e.printStackTrace();
        }
    }

    // Send application rejection email
    public void sendApplicationRejectedEmail(String toEmail, String applicantName, Long applicationId, String rejectionReason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("CareNet - Application Status Update");
            
            String htmlContent = buildApplicationRejectedEmailContent(applicantName, applicationId, rejectionReason);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("Application rejected email sent to: " + toEmail);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send application rejected email to: " + toEmail);
            e.printStackTrace();
        }
    }

    // Send document rejection email
    public void sendDocumentRejectedEmail(String toEmail, String applicantName, Long applicationId, String fileName, String rejectionReason) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("CareNet - Document Rejected: " + fileName);
            
            String htmlContent = buildDocumentRejectedEmailContent(applicantName, applicationId, fileName, rejectionReason);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("Document rejected email sent to: " + toEmail + " for file: " + fileName);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send document rejected email to: " + toEmail + " for file: " + fileName);
            e.printStackTrace();
        }
    }

    private String buildApplicationSubmittedEmailContent(String applicantName, Long applicationId) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #2563eb; margin: 0;'>CareNet</h1>" +
                "<p style='color: #666; margin: 5px 0;'>Professional Caregiver Network</p>" +
                "</div>" +
                "<h2 style='color: #16a34a;'>✅ Application Submitted Successfully!</h2>" +
                "<p>Dear " + applicantName + ",</p>" +
                "<p>Thank you for submitting your caregiver application to CareNet. We have successfully received your application.</p>" +
                "<div style='background: #f0f9ff; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<h3 style='margin-top: 0; color: #0369a1;'>Application Details:</h3>" +
                "<p><strong>Application ID:</strong> #" + applicationId + "</p>" +
                "<p><strong>Submission Date:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")) + "</p>" +
                "<p><strong>Status:</strong> Under Review</p>" +
                "</div>" +
                "<p><strong>What happens next?</strong></p>" +
                "<ul>" +
                "<li>Our review team will carefully examine your application and documents</li>" +
                "<li>The review process typically takes 3-5 business days</li>" +
                "<li>You will receive an email notification once the review is complete</li>" +
                "<li>You can track your application status in your dashboard</li>" +
                "</ul>" +
                "<p>If you have any questions or concerns, please don't hesitate to contact our support team.</p>" +
                "<div style='margin-top: 30px; padding: 20px; background: #f9fafb; border-radius: 8px; text-align: center;'>" +
                "<p style='margin: 0; color: #666;'>Thank you for choosing CareNet!</p>" +
                "<p style='margin: 5px 0; color: #666;'>CareNet Team</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    private String buildApplicationApprovedEmailContent(String applicantName, Long applicationId, String notes) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #2563eb; margin: 0;'>CareNet</h1>" +
                "<p style='color: #666; margin: 5px 0;'>Professional Caregiver Network</p>" +
                "</div>" +
                "<h2 style='color: #16a34a;'>🎉 Congratulations! Your Application Has Been Approved!</h2>" +
                "<p>Dear " + applicantName + ",</p>" +
                "<p>We are excited to inform you that your caregiver application has been <strong>approved</strong>!</p>" +
                "<div style='background: #f0fdf4; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #16a34a;'>" +
                "<h3 style='margin-top: 0; color: #15803d;'>Application Approved ✅</h3>" +
                "<p><strong>Application ID:</strong> #" + applicationId + "</p>" +
                "<p><strong>Approval Date:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")) + "</p>" +
                "<p><strong>Status:</strong> <span style='color: #16a34a; font-weight: bold;'>APPROVED</span></p>" +
                "</div>" +
                (notes != null && !notes.trim().isEmpty() ? 
                "<div style='background: #fffbeb; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<h3 style='margin-top: 0; color: #d97706;'>Review Notes:</h3>" +
                "<p>" + notes + "</p>" +
                "</div>" : "") +
                "<p><strong>What's next?</strong></p>" +
                "<ul>" +
                "<li>You are now officially part of the CareNet caregiver network</li>" +
                "<li>You can access your caregiver dashboard to manage your profile</li>" +
                "<li>Start receiving care assignments based on your qualifications</li>" +
                "<li>Download your caregiver certificate from your dashboard</li>" +
                "</ul>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<p style='background: #2563eb; color: white; padding: 12px 24px; border-radius: 6px; display: inline-block; margin: 0;'>" +
                "<strong>Welcome to the CareNet Family! 🎉</strong>" +
                "</p>" +
                "</div>" +
                "<p>If you have any questions, please feel free to contact our support team.</p>" +
                "<div style='margin-top: 30px; padding: 20px; background: #f9fafb; border-radius: 8px; text-align: center;'>" +
                "<p style='margin: 0; color: #666;'>Congratulations and welcome aboard!</p>" +
                "<p style='margin: 5px 0; color: #666;'>The CareNet Team</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    private String buildApplicationRejectedEmailContent(String applicantName, Long applicationId, String rejectionReason) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #2563eb; margin: 0;'>CareNet</h1>" +
                "<p style='color: #666; margin: 5px 0;'>Professional Caregiver Network</p>" +
                "</div>" +
                "<h2 style='color: #dc2626;'>Application Status Update</h2>" +
                "<p>Dear " + applicantName + ",</p>" +
                "<p>Thank you for your interest in joining the CareNet caregiver network. We have completed the review of your application.</p>" +
                "<div style='background: #fef2f2; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #dc2626;'>" +
                "<h3 style='margin-top: 0; color: #b91c1c;'>Application Status</h3>" +
                "<p><strong>Application ID:</strong> #" + applicationId + "</p>" +
                "<p><strong>Review Date:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")) + "</p>" +
                "<p><strong>Status:</strong> <span style='color: #dc2626; font-weight: bold;'>NOT APPROVED</span></p>" +
                "</div>" +
                "<div style='background: #fffbeb; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<h3 style='margin-top: 0; color: #d97706;'>Reason for Decision:</h3>" +
                "<p>" + (rejectionReason != null && !rejectionReason.trim().isEmpty() ? rejectionReason : "Your application did not meet our current requirements. This may be due to incomplete documentation, qualification mismatch, or other administrative requirements.") + "</p>" +
                "</div>" +
                "<p><strong>What you can do:</strong></p>" +
                "<ul>" +
                "<li>Review the feedback provided above</li>" +
                "<li>Address any specific issues mentioned</li>" +
                "<li>You may reapply in the future once requirements are met</li>" +
                "<li>Contact our support team if you need clarification on the requirements</li>" +
                "</ul>" +
                "<p>We appreciate your interest in CareNet and encourage you to consider reapplying in the future if circumstances change.</p>" +
                "<div style='margin-top: 30px; padding: 20px; background: #f9fafb; border-radius: 8px; text-align: center;'>" +
                "<p style='margin: 0; color: #666;'>Thank you for your interest in CareNet</p>" +
                "<p style='margin: 5px 0; color: #666;'>CareNet Review Team</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    private String buildDocumentRejectedEmailContent(String applicantName, Long applicationId, String fileName, String rejectionReason) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<h1 style='color: #2563eb; margin: 0;'>CareNet</h1>" +
                "<p style='color: #666; margin: 5px 0;'>Professional Caregiver Network</p>" +
                "</div>" +
                "<h2 style='color: #dc2626;'>📄 Document Requires Attention</h2>" +
                "<p>Dear " + applicantName + ",</p>" +
                "<p>We have reviewed one of the documents in your caregiver application and it requires your attention.</p>" +
                "<div style='background: #fef2f2; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #dc2626;'>" +
                "<h3 style='margin-top: 0; color: #b91c1c;'>Document Status</h3>" +
                "<p><strong>Application ID:</strong> #" + applicationId + "</p>" +
                "<p><strong>Document Name:</strong> " + fileName + "</p>" +
                "<p><strong>Review Date:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a")) + "</p>" +
                "<p><strong>Status:</strong> <span style='color: #dc2626; font-weight: bold;'>REJECTED</span></p>" +
                "</div>" +
                "<div style='background: #fffbeb; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<h3 style='margin-top: 0; color: #d97706;'>Reason for Rejection:</h3>" +
                "<p>" + (rejectionReason != null && !rejectionReason.trim().isEmpty() ? rejectionReason : "The document did not meet our requirements. This may be due to unclear image quality, missing information, expired documentation, or format issues.") + "</p>" +
                "</div>" +
                "<p><strong>What you need to do:</strong></p>" +
                "<ul>" +
                "<li>Review the rejection reason above</li>" +
                "<li>Prepare a corrected version of the document</li>" +
                "<li>Upload the new document through your application portal</li>" +
                "<li>Contact support if you need clarification on requirements</li>" +
                "</ul>" +
                "<div style='background: #dbeafe; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<h3 style='margin-top: 0; color: #1d4ed8;'>📋 Important Note:</h3>" +
                "<p style='margin: 0;'>Your entire application has been automatically moved to <strong>REJECTED</strong> status due to this document rejection. Once you upload a corrected document, our team will review your complete application again.</p>" +
                "</div>" +
                "<p>Please address this issue promptly to continue with your application process.</p>" +
                "<div style='margin-top: 30px; padding: 20px; background: #f9fafb; border-radius: 8px; text-align: center;'>" +
                "<p style='margin: 0; color: #666;'>We look forward to your corrected submission</p>" +
                "<p style='margin: 5px 0; color: #666;'>CareNet Review Team</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }
}
