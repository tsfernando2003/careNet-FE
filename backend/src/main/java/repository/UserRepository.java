package com.CareNet.CareNet.repository;

import com.CareNet.CareNet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Find user by email and password for authentication
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password AND u.isActive = true")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
    
    // Find users by role
    List<User> findByRole(User.UserRole role);
    
    // Find active users
    List<User> findByIsActiveTrue();
    
    // Check if email exists
    boolean existsByEmail(String email);
}
