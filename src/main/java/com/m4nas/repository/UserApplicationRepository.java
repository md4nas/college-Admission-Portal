package com.m4nas.repository;

import com.m4nas.model.UserApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserApplicationRepository extends JpaRepository<UserApplication, String> {

    // ===== BASIC USER OPERATIONS =====
    UserApplication findByUserEmail(String userEmail); // Find application by user email
    boolean existsByUserEmail(String userEmail); // Check if user already has an application
    
    // Debug query to get application with all fields
    @Query("SELECT ua FROM UserApplication ua WHERE ua.userEmail = :email")
    UserApplication findByUserEmailWithAllFields(@Param("email") String email);
    List<UserApplication> findByStatus(String status); // Find applications by status
    List<UserApplication> findByAllocatedBranch(String branch); // Find applications by allocated branch

    // ===== TEACHER OPERATIONS (NEW/UPDATED) =====

    // Get applications pending teacher approval (SUBMITTED STATUS)
    @Query("SELECT ua FROM UserApplication ua WHERE ua.status = 'SUBMITTED' ORDER BY ua.obtain12Marks DESC")
    List<UserApplication> findApplicationsPendingApproval();

    // Get approved applications ready for seat allocation
    @Query("SELECT ua FROM UserApplication ua WHERE ua.status = 'APPROVED' ORDER BY ua.obtain12Marks DESC")
    List<UserApplication> findApprovedApplicationsForSeatAllocation();

    // Get allocated applications waiting for student response
    @Query("SELECT ua FROM UserApplication ua WHERE ua.status = 'ALLOCATED' AND ua.seatAccepted = false")
    List<UserApplication> findAllocatedApplicationsPendingResponse();

    // Get applications by multiple statuses (for TEACHER dashboard)
    @Query("SELECT ua FROM UserApplication ua WHERE ua.status IN :statuses ORDER BY ua.obtain12Marks DESC")
    List<UserApplication> findByStatusIn(@Param("statuses") List<String> statuses);

    // ===== ADMIN OPERATIONS (READ-ONLY) =====

    // Get all applications for admin dashboard - using findAll() in service
    // Removed complex query to avoid issues
    
    // Get status-wise count for admin reports
    @Query("SELECT ua.status, COUNT(ua) FROM UserApplication ua GROUP BY ua.status")
    List<Object[]> getApplicationStatusCounts();

    // Get branch-wise statistics for admin
    @Query("SELECT ua.allocatedBranch, COUNT(ua), AVG(ua.obtain12Marks) FROM UserApplication ua WHERE ua.allocatedBranch IS NOT NULL GROUP BY ua.allocatedBranch")
    List<Object[]> getBranchWiseStatistics();

    // ===== LEGACY METHODS (Keep for backward compatibility) =====

    // Find all submitted applications ordered by marks (for ranking)
    @Query("SELECT ua FROM UserApplication ua WHERE ua.status = 'SUBMITTED' ORDER BY ua.obtain12Marks DESC")
    List<UserApplication> findAllSubmittedOrderByMarksDesc();

    // Find applications by status ordered by marks
    List<UserApplication> findByStatusOrderByObtain12MarksDesc(String status);

    // Get all applications for admin & teacher based on ranking (submitted only)
    @Query("SELECT ua FROM UserApplication ua WHERE ua.status = 'SUBMITTED'")
    List<UserApplication> findAllSubmittedApplications();

    // Find applications that need seat allocation (submitted but not allocated)
    @Query("SELECT ua FROM UserApplication ua WHERE ua.status = 'SUBMITTED' AND ua.allocatedBranch IS NULL ORDER BY ua.obtain12Marks DESC")
    List<UserApplication> findApplicationsForSeatAllocation();
    
    List<UserApplication> findAllByOrderBySubmissionDateDesc();
}
