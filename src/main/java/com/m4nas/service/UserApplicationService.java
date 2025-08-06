package com.m4nas.service;

import com.m4nas.model.UserApplication;
import java.util.List;

public interface UserApplicationService {

    // ==== USER OPERATIONS ====
    UserApplication savePersonalInfo(UserApplication application);
    UserApplication saveAcademicInfo(UserApplication application);
    UserApplication getUserApplicationByEmail(String email);
    UserApplication acceptSeat(String applicationId);
    UserApplication declineSeat(String applicationId);
    boolean hasUserSubmittedApplication(String email);

    // ==== TEACHER OPERATIONS ====
    List<UserApplication> getApplicationsPendingApproval();
    List<UserApplication> getApprovedApplicationsForSeatAllocation();
    List<UserApplication> getAllocatedApplicationsPendingResponse();
    UserApplication approveApplication(String applicationId);
    UserApplication rejectApplication(String applicationId);
    UserApplication allocateSeat(String applicationId, String branch);

    // ==== ADMIN OPERATIONS (READ-ONLY) ====
    List<UserApplication> getAllApplicationsForAdmin();
    List<Object[]> getApplicationStatusCounts();
    List<Object[]> getBranchWiseStatistics();
    
    List<UserApplication> getAllApplications();
    
    UserApplication updateApplicationStatus(String applicationId, String status);
    
    UserApplication updateApplicationCourse(String applicationId, String course);
    
    UserApplication updateApplicationBranch(String applicationId, String allocatedBranch);
    List<UserApplication> getApplicationsByStatus(String status);

    // ==== UTILITY METHODS ====
    void calculatePercentages(UserApplication application);
    UserApplication getApplicationById(String applicationId);

}
