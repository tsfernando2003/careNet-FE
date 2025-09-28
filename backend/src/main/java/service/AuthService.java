package com.CareNet.CareNet.service;

import com.CareNet.CareNet.model.User;
import com.CareNet.CareNet.model.Caregiver;
import com.CareNet.CareNet.repository.UserRepository;
import com.CareNet.CareNet.repository.CaregiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CaregiverRepository caregiverRepository;

    public Map<String, Object> authenticate(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // First, try to find user by email only
            Optional<User> userByEmail = userRepository.findByEmail(email);
            
            if (userByEmail.isPresent()) {
                User user = userByEmail.get();
                
                // Check if the user is active (handle null case)
                Boolean isActive = user.getIsActive();
                if (isActive == null || !isActive) {
                    response.put("success", false);
                    response.put("message", "Account is inactive");
                    return response;
                }
                
                // For now, do simple password comparison (in production, use password hashing)
                if (user.getPassword().equals(password)) {
                    response.put("success", true);
                    response.put("message", "Authentication successful");
                    
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("email", user.getEmail());
                    userData.put("firstName", user.getFirstName());
                    userData.put("lastName", user.getLastName());
                    userData.put("role", user.getRole().toString());
                    
                    // If user is a caregiver, include caregiver ID for dashboard access
                    if (user.getRole() == User.UserRole.CAREGIVER && user.getCaregiver() != null) {
                        userData.put("caregiverId", user.getCaregiver().getId());
                    }
                    
                    response.put("user", userData);
                } else {
                    response.put("success", false);
                    response.put("message", "Invalid email or password");
                }
                
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Authentication failed: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> registerCaregiver(User user, Caregiver caregiver) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return response;
            }
            
            // Set user role as CAREGIVER
            user.setRole(User.UserRole.CAREGIVER);
            
            // Save user first
            User savedUser = userRepository.save(user);
            
            // Link caregiver to user
            caregiver.setUser(savedUser);
            // Email is already in User entity, no need to duplicate
            
            // Save caregiver
            caregiverRepository.save(caregiver);
            
            response.put("success", true);
            response.put("message", "Registration successful");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }
        
        return response;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
