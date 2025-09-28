package com.CareNet.CareNet.repository;

import com.CareNet.CareNet.model.Caregiver;
import com.CareNet.CareNet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {

    // Find all applications for a specific user ordered by date
    List<Caregiver> findByUserOrderByApplicationDateDesc(User user);

    // Find applications by user ID
    @Query("SELECT c FROM Caregiver c WHERE c.user.id = :userId ORDER BY c.applicationDate DESC")
    List<Caregiver> findByUserIdOrderByApplicationDateDesc(@Param("userId") Long userId);

    // Find by status (enum)
    List<Caregiver> findByStatus(Caregiver.ApplicationStatus status);

    // Find by caregiver type (enum)
    List<Caregiver> findByCaregiverType(Caregiver.CaregiverType caregiverType);

    // Find by user and status
    List<Caregiver> findByUserAndStatus(User user, Caregiver.ApplicationStatus status);

    // Count applications by user id
    @Query("SELECT COUNT(c) FROM Caregiver c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // Pending applications for admin dashboard (uses enum name in JPQL)
    @Query("SELECT c FROM Caregiver c WHERE c.status = 'PENDING' ORDER BY c.applicationDate ASC")
    List<Caregiver> findPendingApplications();

    // Find caregiver by user email (single canonical declaration)
    @Query("SELECT c FROM Caregiver c WHERE c.user.email = :email")
    Optional<Caregiver> findByEmail(@Param("email") String email);

    // Find caregiver by user id (explicit query to match service usage)
    @Query("SELECT c FROM Caregiver c WHERE c.user.id = :userId")
    Optional<Caregiver> findByUserId(@Param("userId") Long userId);

    // All caregivers for given user id
    List<Caregiver> findAllByUserId(Long userId);
}
