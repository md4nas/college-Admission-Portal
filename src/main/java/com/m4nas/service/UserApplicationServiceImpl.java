package com.m4nas.service;

import com.m4nas.model.UserApplication;
import com.m4nas.repository.UserApplicationRepository;
import com.m4nas.util.RandomString;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class UserApplicationServiceImpl implements  UserApplicationService{

    @Autowired
    private UserApplicationRepository userApplicationRepo;

    @Override
    public UserApplication savePersonalInfo(UserApplication application) {
        // Generate ID if new application
        if(application.getId() == null){
            application.setId(RandomString.generateUserId());
            application.setStatus("PENDING");
        }
        return userApplicationRepo.save(application);
    }

    @Override
    public UserApplication saveAcademicInfo(UserApplication application) {
        // Generate ID if new application
        if(application.getId() == null || application.getId().isEmpty()){
            application.setId(RandomString.generateUserId());
        }
        
        //calculate percentage automatically
        calculatePercentages(application);

        //change status to SUBMITTED when academic info is completed
        application.setStatus("SUBMITTED");

        return userApplicationRepo.save(application);
    }

    @Override
    public UserApplication getUserApplicationByEmail(String email) {
        System.out.println("=== SERVICE DEBUG ===");
        System.out.println("Looking for application with email: " + email);
        UserApplication app = userApplicationRepo.findByUserEmail(email);
        System.out.println("Found application: " + (app != null ? "YES - ID: " + app.getId() : "NO"));
        return app;
    }

    @Override
    public UserApplication acceptSeat(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "ALLOCATED".equals(app.getStatus())){
            app.setSeatAccepted(true);
            app.setStatus("ACCEPTED");
            return userApplicationRepo.save(app);
        }
        return null; // Fixed: return null if app is null or wrong status
    }

    @Override
    public UserApplication declineSeat(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "ALLOCATED".equals(app.getStatus())){
            app.setSeatAccepted(false);
            app.setStatus("DECLINED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    @Override
    public boolean hasUserSubmittedApplication(String email) {
        return userApplicationRepo.existsByUserEmail(email);
    }

    // ==== TEACHER OPERATION ====

    @Override
    public List<UserApplication> getApplicationsPendingApproval() {
        return userApplicationRepo.findApplicationsPendingApproval();
    }

    @Override
    public List<UserApplication> getApprovedApplicationsForSeatAllocation() {
        return userApplicationRepo.findApprovedApplicationsForSeatAllocation();
    }

    @Override
    public List<UserApplication> getAllocatedApplicationsPendingResponse() {
        return userApplicationRepo.findAllocatedApplicationsPendingResponse();
    }

    @Override
    public UserApplication approveApplication(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "SUBMITTED".equals(app.getStatus())){
            app.setStatus("APPROVED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    @Override
    public UserApplication rejectApplication(String applicationId) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "SUBMITTED".equals(app.getStatus())){
            app.setStatus("REJECTED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    @Override
    public UserApplication allocateSeat(String applicationId, String branch) {
        UserApplication app = userApplicationRepo.findById(applicationId).orElse(null);
        if (app != null && "APPROVED".equals(app.getStatus())){
            app.setAllocatedBranch(branch);
            app.setStatus("ALLOCATED");
            return userApplicationRepo.save(app);
        }
        return null;
    }

    // ===== ADMIN OPERATIONS (READ-ONLY) =====

    @Override
    public List<UserApplication> getAllApplicationsForAdmin() {
        try {
            return userApplicationRepo.findAll();
        } catch (Exception e) {
            System.err.println("Error getting all applications: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getApplicationStatusCounts() {
        try {
            List<Object[]> result = userApplicationRepo.getApplicationStatusCounts();
            if (result == null || result.isEmpty()) {
                // Fallback: create manual count
                List<UserApplication> allApps = userApplicationRepo.findAll();
                java.util.Map<String, Long> statusMap = new java.util.HashMap<>();
                for (UserApplication app : allApps) {
                    statusMap.put(app.getStatus(), statusMap.getOrDefault(app.getStatus(), 0L) + 1);
                }
                List<Object[]> fallbackResult = new java.util.ArrayList<>();
                for (java.util.Map.Entry<String, Long> entry : statusMap.entrySet()) {
                    fallbackResult.add(new Object[]{entry.getKey(), entry.getValue()});
                }
                return fallbackResult;
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error getting status counts: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<Object[]> getBranchWiseStatistics() {
        try {
            List<Object[]> result = userApplicationRepo.getBranchWiseStatistics();
            if (result == null || result.isEmpty()) {
                // Fallback: create manual statistics
                List<UserApplication> allocatedApps = userApplicationRepo.findAll().stream()
                    .filter(app -> app.getAllocatedBranch() != null)
                    .collect(java.util.stream.Collectors.toList());
                
                java.util.Map<String, java.util.List<UserApplication>> branchMap = new java.util.HashMap<>();
                for (UserApplication app : allocatedApps) {
                    branchMap.computeIfAbsent(app.getAllocatedBranch(), k -> new java.util.ArrayList<>()).add(app);
                }
                
                List<Object[]> fallbackResult = new java.util.ArrayList<>();
                for (java.util.Map.Entry<String, java.util.List<UserApplication>> entry : branchMap.entrySet()) {
                    String branch = entry.getKey();
                    List<UserApplication> apps = entry.getValue();
                    long count = apps.size();
                    double avgMarks = apps.stream().mapToInt(UserApplication::getObtain12Marks).average().orElse(0.0);
                    fallbackResult.add(new Object[]{branch, count, avgMarks});
                }
                return fallbackResult;
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error getting branch statistics: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public List<UserApplication> getApplicationsByStatus(String status) {
        try {
            return userApplicationRepo.findByStatus(status);
        } catch (Exception e) {
            System.err.println("Error getting applications by status: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // ===== UTILITY METHODS =====

    @Override
    public void calculatePercentages(UserApplication application) {
        // Calculate class 10 percentage
        if (application.getTotal10Marks() != null && application.getObtain10Marks() != null
        && application.getTotal10Marks() > 0 ) {
            double percentage10 = (application.getObtain10Marks().doubleValue() /
                    application.getTotal10Marks().doubleValue()) * 100;
            application.setPercentage10(Math.round(percentage10 * 100.0) / 100.0);
        }

        // Calculate Class 12 percentage
        if (application.getTotal12Marks() != null && application.getObtain12Marks() != null
                && application.getTotal12Marks() > 0) {
            double percentage12 = (application.getObtain12Marks().doubleValue() /
                    application.getTotal12Marks().doubleValue()) * 100;
            application.setPercentage12(Math.round(percentage12 * 100.0) / 100.0);
        }

    }
}
